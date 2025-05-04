package tn.esprit.educareer.controllers.Reclamation;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Reclamation;
import tn.esprit.educareer.models.TypeReclamation;
import tn.esprit.educareer.services.ReclamationService;

import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

public class AjouterReclamation {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML private TextField sujet;
    @FXML private TextField description;
    @FXML private ComboBox<TypeReclamation> typeReclamation;
    @FXML private Label recordingStatus;
    @FXML private Button btnVoice;

    private List<TypeReclamation> allTypes;
    private ReclamationService reclamationService;

    // bad words
    private final List<String> badWords = List.of("insulte", "idiot", "stupide");

    // voice recognition
    private volatile boolean isRecording = false;
    private Thread recognitionThread;
    private Model voiceModel;
    private Recognizer voiceRecognizer;

    @FXML
    public void initialize() {
        // types
        reclamationService = new ReclamationService();
        allTypes = reclamationService.getAllTypes();
        typeReclamation.getItems().addAll(allTypes);

        // voice controls init
        recordingStatus.setText("Prêt");
        initVoiceRecognition();
    }

    @FXML
    private void handleTyping(KeyEvent event) {
        suggestType();
    }

    private void suggestType() {
        String texte = (sujet.getText() + " " + description.getText()).toLowerCase();
        for (TypeReclamation type : allTypes) {
            if (texte.contains(type.getNom().toLowerCase())) {
                typeReclamation.setValue(type);
                return;
            }
        }
        typeReclamation.setValue(null);
    }

    private String censorBadWords(String text) {
        String result = text;
        for (String word : badWords) {
            result = result.replaceAll("(?i)\\b" + word + "\\b", "***");
        }
        return result;
    }

    @FXML
    private void save(ActionEvent event) {
        if (sujet.getText().isEmpty() || description.getText().isEmpty() || typeReclamation.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs !");
            return;
        }

        // censor
        String censoredDescription = censorBadWords(description.getText());

        Reclamation newRec = new Reclamation();
        newRec.setSujet(sujet.getText());
        newRec.setDescription(censoredDescription);
        newRec.setTypeReclamation(typeReclamation.getValue());
        newRec.setCreatedAt(LocalDateTime.now());

        reclamationService.ajouter(newRec);
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation enregistrée (mots inappropriés censurés) !");

        // navigate
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/Student.fxml"));
            Parent root = loader.load();
            sujet.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la navigation : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/User/Student.fxml");
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) throw new IOException("FXML file not found: " + path);
        root = FXMLLoader.load(fxmlLocation);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private void initVoiceRecognition() {
        try {
            LibVosk.setLogLevel(LogLevel.WARNINGS);
            File modelDir = new File("src/main/resources/vosk-model-fr");
            if (!modelDir.exists() || !modelDir.isDirectory() || modelDir.list() == null || modelDir.list().length == 0) {
                System.err.println("Le dossier du modèle Vosk est vide ou n'existe pas : " + modelDir.getAbsolutePath());
                btnVoice.setDisable(true);
                btnVoice.setTooltip(new Tooltip("Modèle de reconnaissance vocale non disponible"));
                showAlert(Alert.AlertType.WARNING, "Erreur", "Le modèle de reconnaissance vocale n'est pas disponible.");
                return;
            }
            voiceModel = new Model(modelDir.getAbsolutePath());
            btnVoice.setDisable(false);
            btnVoice.setTooltip(new Tooltip("Cliquez pour démarrer/arrêter la reconnaissance vocale"));
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de la reconnaissance vocale: " + e.getMessage());
            e.printStackTrace();
            btnVoice.setDisable(true);
            btnVoice.setTooltip(new Tooltip("Erreur: " + e.getMessage()));
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'initialisation de la reconnaissance vocale: " + e.getMessage());
        }
    }

    @FXML
    public void handleVoiceButtonAction(ActionEvent event) {
        if (isRecording) {
            stopRecording();
            btnVoice.setText("Démarrer reconnaissance");
            recordingStatus.setText("Prêt");
            btnVoice.getStyleClass().remove("recording");
        } else {
            startVoiceRecognition();
            btnVoice.setText("Arrêter reconnaissance");
            recordingStatus.setText("Enregistrement...");
            btnVoice.getStyleClass().add("recording");
        }
    }

    private void startVoiceRecognition() {
        try {
            if (voiceModel == null) {
                String modelPath = "src/main/resources/vosk-model-fr";
                File modelDir = new File(modelPath);
                if (!modelDir.exists() || !modelDir.isDirectory() || modelDir.list().length == 0) {
                    System.err.println(" Erreur: Le répertoire du modèle n'existe pas ou est vide: " + modelPath);
                    btnVoice.setDisable(true);
                    btnVoice.setTooltip(new Tooltip("Le modèle de reconnaissance vocale n'est pas disponible"));
                    showAlert(Alert.AlertType.WARNING, "Erreur", "Le modèle de reconnaissance vocale n'est pas disponible. Veuillez installer le modèle Vosk français.");
                    return;
                }
                System.out.println(" Initialisation du modèle Vosk...");
                LibVosk.setLogLevel(LogLevel.DEBUG);
                voiceModel = new Model(modelPath);
                System.out.println(" Modèle chargé avec succès");
            }

            System.out.println("Configuration de l'audio...");
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.err.println(" Erreur: Format audio non supporté");
                showAlert(Alert.AlertType.WARNING, "Erreur", "Le microphone n'est pas supporté");
                return;
            }

            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            System.out.println("Capture audio démarrée");

            isRecording = true;
            voiceRecognizer = new Recognizer(voiceModel, 16000);

            recognitionThread = new Thread(() -> {
                byte[] buffer = new byte[4096];
                try {
                    System.out.println(" Démarrage de la reconnaissance vocale");
                    while (isRecording) {
                        int count = line.read(buffer, 0, buffer.length);
                        if (count > 0 && voiceRecognizer != null) {
                            if (voiceRecognizer.acceptWaveForm(buffer, count)) {
                                String result = voiceRecognizer.getResult();
                                System.out.println("Résultat reçu: " + result);
                                JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
                                String text = jsonObject.get("text").getAsString();

                                if (!text.isEmpty()) {
                                    // Conversion d'encodage pour gérer les caractères français
                                    byte[] bytes = text.getBytes("ISO-8859-1");
                                    String utf8Text = new String(bytes, "UTF-8");
                                    System.out.println("Texte reconnu (avant conversion): " + text);
                                    System.out.println("Texte reconnu (après conversion UTF-8): " + utf8Text);

                                    Platform.runLater(() -> {
                                        String currentText = description.getText();
                                        description.setText(currentText + (currentText.isEmpty() ? "" : " ") + utf8Text);
                                        suggestType(); // Mettre à jour la suggestion de type après reconnaissance
                                    });
                                }
                            }
                        }
                    }

                    String finalResult = voiceRecognizer.getFinalResult();
                    System.out.println("Résultat final: " + finalResult);
                    JsonObject jsonObject = JsonParser.parseString(finalResult).getAsJsonObject();
                    String finalText = jsonObject.get("text").getAsString();

                    if (!finalText.isEmpty()) {
                        // Conversion pour le résultat final
                        byte[] bytes = finalText.getBytes("ISO-8859-1");
                        String utf8FinalText = new String(bytes, "UTF-8");
                        System.out.println("Texte final (avant conversion): " + finalText);
                        System.out.println("Texte final (après conversion UTF-8): " + utf8FinalText);

                        Platform.runLater(() -> {
                            String currentText = description.getText();
                            description.setText(currentText + (currentText.isEmpty() ? "" : " ") + utf8FinalText);
                            suggestType(); // Mettre à jour la suggestion de type
                        });
                    }
                } catch (Exception e) {
                    System.err.println(" Erreur pendant la reconnaissance vocale: " + e.getMessage());
                    e.printStackTrace();
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la reconnaissance vocale: " + e.getMessage()));
                } finally {
                    System.out.println(" Nettoyage des ressources audio");
                    line.stop();
                    line.close();
                    if (voiceRecognizer != null) {
                        voiceRecognizer.close();
                        voiceRecognizer = null;
                    }
                }
            });

            recognitionThread.setDaemon(true);
            recognitionThread.start();

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation de la reconnaissance vocale: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'initialisation de la reconnaissance vocale: " + e.getMessage());
            isRecording = false;
        }
    }

    private void stopRecording() {
        isRecording = false;
        if (recognitionThread != null) {
            try {
                recognitionThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    public void cleanup() {
        if (voiceRecognizer != null) {
            voiceRecognizer.close();
            voiceRecognizer = null;
        }
        if (voiceModel != null) {
            voiceModel.close();
            voiceModel = null;
        }
    }
}
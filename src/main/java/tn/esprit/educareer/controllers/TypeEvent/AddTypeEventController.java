package tn.esprit.educareer.controllers.TypeEvent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import tn.esprit.educareer.models.TypeEvent;
import tn.esprit.educareer.services.ServiceTypeEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddTypeEventController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField nomEField;

    @FXML
    private Button typeeventadd;

    private final ServiceTypeEvent serviceTypeEvent = new ServiceTypeEvent();

    @FXML
    void typeeventadd(ActionEvent event) {
        String nomE = nomEField.getText().trim();

        if (nomE.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom du type ne peut pas être vide.");
            return;
        }

        TypeEvent newTypeEvent = new TypeEvent();
        newTypeEvent.setNomE(nomE);

        serviceTypeEvent.ajouter(newTypeEvent);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Type d'événement ajouté avec succès.");
        goBackToTypeEventList();
    }

    private void goBackToTypeEventList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TypeEvent/TypeEventList.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) typeeventadd.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene); // <-- ADD THIS LINE
            stage.centerOnScreen(); // optional: to center the window
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de revenir à la liste des types.");
        }
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Valider le champ de texte pour n'accepter que les lettres et les espaces
        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            // Expression régulière pour autoriser uniquement les lettres et les espaces
            if (change.getControlNewText().matches("[a-zA-Z\\s]*")) {
                return change;
            }
            return null;  // Refuser les changements qui ne respectent pas l'expression régulière
        });

        // Appliquer le TextFormatter au champ de texte
        nomEField.setTextFormatter(textFormatter);
    }
    @FXML
    private Button returnButton;
    @FXML
    void handleReturn(ActionEvent event) throws IOException {
        navigateToPage(event, "/TypeEvent/TypeEventList.fxml");
    }


    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        root = FXMLLoader.load(getClass().getResource(path));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}

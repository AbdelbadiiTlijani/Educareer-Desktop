package tn.esprit.educareer.controllers.cours;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.educareer.models.CategorieCours;
import tn.esprit.educareer.models.Cours;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ServiceCategorieCours;
import tn.esprit.educareer.services.ServiceCours;
import tn.esprit.educareer.services.ServiceUser;
import tn.esprit.educareer.utils.UserSession;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ajouterCoursController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField nomCours;
    @FXML
    private Label nomCoursErrorLabel;

    @FXML
    private TextField requirementCours;
    @FXML
    private Label requirementCoursErrorLabel;


    @FXML
    private File selectedDocumentFile;
    @FXML
    private Label documentFileNameLabel;
    @FXML
    private Label documentCoursErrorLabel;


    @FXML
    private File selectedImageFile;
    @FXML
    private Label imageFileNameLabel;
    @FXML
    private Label imageCoursErrorLabel;


    @FXML
    private ComboBox<CategorieCours> categorieComboBox;
    @FXML
    private Label categorieErrorLabel;


    @FXML
    private Button ajoutCoursButton;
    @FXML
    private Button backButton;


    @FXML
    private Label globalErrorLabel;


    private String errorStyle = "-fx-border-color: red; -fx-border-width: 2px;";
    private String originalStyle = "";

    private ServiceCours serviceCours = new ServiceCours();
    private ServiceCategorieCours serviceCategorieCours = new ServiceCategorieCours();

    @FXML
    void initialize() {
        categorieComboBox.getItems().addAll(serviceCategorieCours.getAll());
        categorieComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(CategorieCours item, boolean empty) {
                super.updateItem(item, empty);
                setText(String.valueOf(empty || item == null ? null : item.getNom()));
            }
        });

        categorieComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CategorieCours item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });

        backButton.setOnAction(e -> goBack());
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        root = FXMLLoader.load(fxmlLocation);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root , 1000 , 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    private void handleChooseDocument() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier document");

        // Filtrage pour ne permettre que les fichiers PDF
        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.docx", "*.txt");
        fileChooser.getExtensionFilters().add(pdfFilter);

        selectedDocumentFile = fileChooser.showOpenDialog(null);
        if (selectedDocumentFile != null) {
            // Vérifier l'extension
            if (!selectedDocumentFile.getName().endsWith(".pdf")) {
                documentCoursErrorLabel.setText("Seuls les fichiers PDF sont autorisés.");
                selectedDocumentFile = null;  // Réinitialiser la sélection
            } else {
                documentFileNameLabel.setText(selectedDocumentFile.getName());
            }
        }
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");

        // Filtrage pour ne permettre que les fichiers PNG
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(pngFilter);

        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            // Vérifier l'extension
            if (!selectedImageFile.getName().endsWith(".png")) {
                imageCoursErrorLabel.setText("Seuls les fichiers PNG sont autorisés.");
                selectedImageFile = null;  // Réinitialiser la sélection
            } else {
                imageFileNameLabel.setText(selectedImageFile.getName());
            }
        }
    }

    User currentUser = UserSession.getInstance().getCurrentUser();
    @FXML
    void handleAddCourseButton(ActionEvent event) throws IOException {

        resetErrorMessages();
        boolean isValid = true;
        String nom = nomCours.getText().trim();
        String requirement = requirementCours.getText().trim();
        String document = (selectedDocumentFile != null) ? selectedDocumentFile.getAbsolutePath().trim() : null;
        String image = (selectedImageFile != null) ? selectedImageFile.getAbsolutePath().trim() : null;
        CategorieCours categorie = categorieComboBox.getValue();
        User formatteur = currentUser;


        if (nom.isEmpty()) {
            nomCoursErrorLabel.setText("Le nom est obligatoire");
            nomCours.setStyle(errorStyle);
            isValid = false;
        }


        if (requirement.isEmpty()) {
            requirementCoursErrorLabel.setText("Les exigences sont obligatoires");
            requirementCours.setStyle(errorStyle);
            isValid = false;
        }
        if (document == null || document.isEmpty()) {
            documentCoursErrorLabel.setText("Veuillez sélectionner un document");
            isValid = false;
        }

        if (image == null || image.isEmpty()) {
            imageCoursErrorLabel.setText("Veuillez sélectionner une image");
            isValid = false;
        }

        if (categorie == null) {
            categorieErrorLabel.setText("Veuillez sélectionner une catégorie");
            globalErrorLabel.setVisible(true);
            isValid = false;
        }



        if (!isValid) {
            globalErrorLabel.setText("Veuillez corriger les erreurs du formulaire.");
            globalErrorLabel.setVisible(true);
            return;
        }

        // Créer un nouveau cours
        Cours cours = new Cours(nom, document, image, requirement, formatteur , categorie);

        // Sauvegarder via le service
        serviceCours.ajouter(cours);

        // Affichage message succès
        globalErrorLabel.setText("Cours ajouté avec succès !");
        globalErrorLabel.setStyle("-fx-text-fill: green;");
        globalErrorLabel.setVisible(true);

        // Réinitialiser les champs
        nomCours.clear();
        requirementCours.clear();
        selectedDocumentFile = null;
        selectedImageFile = null;
        categorieComboBox.getSelectionModel().clearSelection();


        // Naviguer vers la liste des cours
        navigateToPage(event, "/cours/listCours.fxml");
    }

    private void resetErrorMessages() {
        // Labels d'erreur
        nomCoursErrorLabel.setText("");
        documentCoursErrorLabel.setText("");
        imageCoursErrorLabel.setText("");
        requirementCoursErrorLabel.setText("");
        categorieErrorLabel.setText("");

        // Réinitialisation des styles
        nomCours.setStyle(originalStyle);
        documentFileNameLabel.setStyle(originalStyle);
        imageFileNameLabel.setStyle(originalStyle);
        requirementCours.setStyle(originalStyle);
        categorieComboBox.setStyle(originalStyle);

        // Message d'erreur global
        globalErrorLabel.setText("");
        globalErrorLabel.setVisible(false);
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/FormateurDashboard.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 1000, 700);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

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

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class updateCoursController {

    private final ServiceCours serviceCours = new ServiceCours();
    private final ServiceCategorieCours serviceCategorie = new ServiceCategorieCours();
    private final ServiceUser serviceUser = new ServiceUser();
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField nomCours;
    @FXML
    private Label nomCoursErrorLabel;
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
    private Button updateCoursButton;
    @FXML
    private Button backButton;
    @FXML
    private Label globalErrorLabel;
    private String errorStyle = "-fx-border-color: red; -fx-border-width: 2px;";
    private String originalStyle = "";
    @FXML
    private Button chooseDocumentButton;
    @FXML
    private Button chooseImageButton;


    @FXML
    private ComboBox<User> formateurComboBox;
    @FXML
    private Label formateurErrorLabel;


    @FXML
    private TextField requirementCours;
    @FXML
    private Label requirementCoursErrorLabel;


    private Cours selectedCours;

    // Méthode pour choisir le document
    @FXML
    private void handleChooseDocument() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier document");
        selectedDocumentFile = fileChooser.showOpenDialog(null);
        if (selectedDocumentFile != null) {
            documentFileNameLabel.setText(selectedDocumentFile.getName());
        }
    }

    // Méthode pour choisir l'image
    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png"));
        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            imageFileNameLabel.setText(selectedImageFile.getName());
        }
    }


    public void initData(Cours cours) {
        this.selectedCours = cours;
        nomCours.setText(cours.getNom());

        requirementCours.setText(cours.getRequirement());

        if (cours.getDocument() != null) {
            documentFileNameLabel.setText(new File(cours.getDocument()).getName());
        }

        if (cours.getImage() != null) {
            imageFileNameLabel.setText(new File(cours.getImage()).getName());
        }


        categorieComboBox.getItems().addAll(serviceCategorie.getAll());
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


        formateurComboBox.getItems().addAll(serviceUser.getAll());
        formateurComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
        formateurComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
    }

    // Méthode pour la mise à jour du cours
    @FXML
    private void handleUpdateCours(ActionEvent event) throws IOException {
        // Réinitialiser les messages d'erreur
        resetErrorMessages();
        boolean isValid = true;

        // Récupérer les données du formulaire
        String nom = nomCours.getText().trim();
        String requirement = requirementCours.getText().trim();
        CategorieCours categorie = categorieComboBox.getValue();
        User formateur = formateurComboBox.getValue();

        // Vérification du nom du cours
        if (nom.isEmpty()) {
            nomCoursErrorLabel.setText("Le nom du cours est obligatoire");
            nomCours.setStyle(errorStyle);
            isValid = false;
        }

        // Vérification des exigences
        if (requirement.isEmpty()) {
            requirementCoursErrorLabel.setText("Les exigences sont obligatoires");
            requirementCours.setStyle(errorStyle);
            isValid = false;
        }

        // Vérification du document (seulement PDF)
        if (selectedDocumentFile != null && !selectedDocumentFile.getName().endsWith(".pdf")) {
            documentCoursErrorLabel.setText("Seuls les fichiers PDF sont autorisés");
            isValid = false;
        }

        // Vérification de l'image (seulement PNG)
        if (selectedImageFile != null && !selectedImageFile.getName().endsWith(".png")) {
            imageCoursErrorLabel.setText("Seules les images PNG sont autorisées");
            isValid = false;
        }

        // Vérification de la catégorie
        if (categorie == null) {
            categorieErrorLabel.setText("Veuillez sélectionner une catégorie");
            isValid = false;
        }

        // Vérification du formateur
        if (formateur == null) {
            formateurErrorLabel.setText("Veuillez sélectionner un formateur");
            isValid = false;
        }

        // Si des erreurs sont présentes, ne pas continuer
        if (!isValid) {
            globalErrorLabel.setText("Veuillez corriger les erreurs du formulaire.");
            globalErrorLabel.setVisible(true);
            return;
        }

        // Mise à jour des champs du cours
        selectedCours.setNom(nom);
        selectedCours.setRequirement(requirement);
        selectedCours.setCategorie(categorie);
        selectedCours.setUser(formateur);

        // Mettre à jour le document si un nouveau fichier est sélectionné
        if (selectedDocumentFile != null) {
            selectedCours.setDocument(selectedDocumentFile.getAbsolutePath());
        }

        // Mettre à jour l'image si une nouvelle image est sélectionnée
        if (selectedImageFile != null) {
            selectedCours.setImage(selectedImageFile.getAbsolutePath());
        }

        // Mise à jour du cours via le service
        serviceCours.modifier(selectedCours);

        // Affichage de la confirmation de succès
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Le cours a été mis à jour avec succès !");
        alert.showAndWait();

        // Redirection vers la liste des cours
        navigateToPage(event, "/cours/listCours.fxml");
    }

    // Réinitialiser les messages d'erreur
    private void resetErrorMessages() {
        nomCoursErrorLabel.setText("");
        requirementCoursErrorLabel.setText("");
        documentCoursErrorLabel.setText("");
        imageCoursErrorLabel.setText("");
        categorieErrorLabel.setText("");
        formateurErrorLabel.setText("");
        globalErrorLabel.setText("");
        globalErrorLabel.setVisible(false);

        nomCours.setStyle(originalStyle);
        requirementCours.setStyle(originalStyle);
        categorieComboBox.setStyle(originalStyle);
        documentFileNameLabel.setStyle(originalStyle);
        imageFileNameLabel.setStyle(originalStyle);
        formateurComboBox.setStyle(originalStyle);
        categorieComboBox.setStyle(originalStyle);
    }

    // Méthode pour naviguer vers une autre page
    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        root = FXMLLoader.load(fxmlLocation);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    // Méthode pour revenir à la page précédente
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        navigateToPage(event, "/cours/listCours.fxml");
    }
}

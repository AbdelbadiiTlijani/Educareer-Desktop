package tn.esprit.educareer.controllers.cours;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Cours;
import tn.esprit.educareer.models.CategorieCours;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ServiceCours;
import tn.esprit.educareer.services.ServiceCategorieCours;

import java.io.IOException;
import java.net.URL;

public class ajouterCoursController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField nomCours;

    @FXML
    private TextField documentCours;

    @FXML
    private TextField imageCours;

    @FXML
    private TextField requirementCours;

    @FXML
    private ComboBox<CategorieCours> categorieComboBox;
    @FXML
    private ComboBox<User> formateurComboBox;

    @FXML
    private Button ajoutCoursButton;

    @FXML
    private Button backButton;

    @FXML
    private Label globalErrorLabel;
    @FXML
    private Label categorieErrorLabel;
    @FXML
    private Label formateurErrorLabel;

    @FXML
    private Label nomCoursErrorLabel;
    @FXML
    private Label documentCoursErrorLabel;
    @FXML
    private Label imageCoursErrorLabel;
    @FXML
    private Label requirementCoursErrorLabel;

    private String errorStyle = "-fx-border-color: red; -fx-border-width: 2px;";
    private String originalStyle = "";

    private ServiceCours serviceCours = new ServiceCours();
    private ServiceCategorieCours serviceCategorieCours = new ServiceCategorieCours();

    @FXML
    void initialize() {
        // Charger les catégories de cours dans le ComboBox
        categorieComboBox.getItems().addAll(serviceCategorieCours.getAll());
        backButton.setOnAction(e -> goBack());
    }

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

    @FXML
    void handleAddCourseButton(ActionEvent event) throws IOException {

        resetErrorMessages();

        boolean isValid = true;
        String nom = nomCours.getText().trim();
        String document = documentCours.getText().trim();
        String image = imageCours.getText().trim();
        String requirement = requirementCours.getText().trim();
        CategorieCours categorie = categorieComboBox.getValue();

        if (nom.isEmpty()) {
            nomCoursErrorLabel.setText("Le nom est obligatoire");
            nomCours.setStyle(errorStyle);
            isValid = false;
        }

        if (document.isEmpty()) {
            documentCoursErrorLabel.setText("Le document est obligatoire");
            documentCours.setStyle(errorStyle);
            isValid = false;
        }

        if (image.isEmpty()) {
            imageCoursErrorLabel.setText("L'image est obligatoire");
            imageCours.setStyle(errorStyle);
            isValid = false;
        }

        if (requirement.isEmpty()) {
            requirementCoursErrorLabel.setText("Les exigences sont obligatoires");
            requirementCours.setStyle(errorStyle);
            isValid = false;
        }

        if (categorie == null) {
            globalErrorLabel.setText("Veuillez sélectionner une catégorie");
            globalErrorLabel.setVisible(true);
            isValid = false;
        }

        if (!isValid) {
            globalErrorLabel.setText("Veuillez corriger les erreurs du formulaire.");
            globalErrorLabel.setVisible(true);
            return;
        }

        // Créer un nouveau cours
        Cours cours = new Cours(nom, document, image, requirement, categorie, null); // Ajoute l'utilisateur si nécessaire

        // Sauvegarder via le service
        serviceCours.ajouter(cours);

        // Affichage message succès
        globalErrorLabel.setText("Cours ajouté avec succès !");
        globalErrorLabel.setStyle("-fx-text-fill: green;");
        globalErrorLabel.setVisible(true);

        // Réinitialiser les champs
        nomCours.clear();
        documentCours.clear();
        imageCours.clear();
        requirementCours.clear();
        categorieComboBox.getSelectionModel().clearSelection();

        // Naviguer vers la liste des cours
        navigateToPage(event, "/cours/listCours.fxml");
    }

    private void resetErrorMessages() {
        nomCoursErrorLabel.setText("");
        documentCoursErrorLabel.setText("");
        imageCoursErrorLabel.setText("");
        requirementCoursErrorLabel.setText("");
        nomCours.setStyle(originalStyle);
        documentCours.setStyle(originalStyle);
        imageCours.setStyle(originalStyle);
        requirementCours.setStyle(originalStyle);
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

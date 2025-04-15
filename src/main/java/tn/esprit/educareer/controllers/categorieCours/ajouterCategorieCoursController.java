package tn.esprit.educareer.controllers.categorieCours;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.educareer.models.CategorieCours;
import tn.esprit.educareer.services.ServiceCategorieCours;

import java.io.IOException;
import java.net.URL;

public class ajouterCategorieCoursController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField NomCatégorie;

    @FXML
    private Button ajoutCategorieCours;

    @FXML
    private Button backButton;

    @FXML
    private Label globalErrorLabel;

    @FXML
    private Label nomCategorieErrorLabel;

    private String errorStyle = "-fx-border-color: red; -fx-border-width: 2px;";
    private String originalStyle = "";

    private ServiceCategorieCours serviceCategorieCours = new ServiceCategorieCours();

    @FXML
    void initialize() {
//        ajoutCategorieCours.setOnAction(this::handleAjoutCategorieCours);
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
    void handleAjoutCategorieCours(ActionEvent event) throws IOException {
        // Nettoyer les messages d'erreur
        resetErrorMessages();

        boolean isValid = true;
        String nom = NomCatégorie.getText().trim();

        if (nom.isEmpty()) {
            nomCategorieErrorLabel.setText("Le nom est obligatoire");
            NomCatégorie.setStyle(errorStyle);
            isValid = false;
        } else if (nom.length() < 2) {
            nomCategorieErrorLabel.setText("Le nom doit contenir au moins 2 caractères");
            NomCatégorie.setStyle(errorStyle);
            isValid = false;
        }

        if (!isValid) {
            globalErrorLabel.setText("Veuillez corriger les erreurs du formulaire.");
            globalErrorLabel.setVisible(true);
            return;
        }

        // Créer une nouvelle catégorie
        CategorieCours categorie = new CategorieCours();
        categorie.setNom(nom);

        // Sauvegarder via le service
        serviceCategorieCours.ajouter(categorie);

        // Affichage message succès
        globalErrorLabel.setText("Catégorie ajoutée avec succès !");
        globalErrorLabel.setStyle("-fx-text-fill: green;");
        globalErrorLabel.setVisible(true);

        // Réinitialiser le champ
        NomCatégorie.clear();
        navigateToPage(event, "/categorieCours/listCategorieCours.fxml");
    }


    private void resetErrorMessages() {
        nomCategorieErrorLabel.setText("");
        NomCatégorie.setStyle(originalStyle);
        globalErrorLabel.setText("");
        globalErrorLabel.setVisible(false);
        globalErrorLabel.setStyle("-fx-text-fill: red;");
    }


    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
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

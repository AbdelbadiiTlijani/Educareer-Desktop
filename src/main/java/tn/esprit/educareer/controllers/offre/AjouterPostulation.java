package tn.esprit.educareer.controllers.offre;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Offre_Emploi;
import tn.esprit.educareer.models.Postulation;
import tn.esprit.educareer.models.Type_Offre;
import tn.esprit.educareer.services.OffreEmploiService;
import tn.esprit.educareer.services.PostulationService;

import java.io.IOException;
import java.net.URL;

public class AjouterPostulation {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField nom;

    @FXML
    private TextField prenom;

    @FXML
    private TextField resume;

    @FXML
    private ComboBox<Offre_Emploi> offreComboBox;

    private OffreEmploiService offreService = new OffreEmploiService();
    private PostulationService postulationService = new PostulationService();

    // Initialisation automatique quand la page est chargée
    @FXML
    public void initialize() {
        chargerOffresDisponibles();
    }

    // Charger les offres d'emploi dans le ComboBox
    private void chargerOffresDisponibles() {
        offreComboBox.getItems().setAll(offreService.getAll());
    }

    @FXML
    public void save(ActionEvent event) throws IOException {
        String nomText = nom.getText();
        String prenomText = prenom.getText();
        String resumeText = resume.getText();
        Offre_Emploi selectedOffre = offreComboBox.getValue();



        if (nomText.isEmpty() || prenomText.isEmpty() || resumeText.isEmpty() || selectedOffre == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs et sélectionner une offre.");
            alert.showAndWait();
            return;
        }

        Postulation newPostulation = new Postulation(nomText, prenomText, resumeText, selectedOffre);

        postulationService.ajouter(newPostulation);

        navigateToPage(event, "/offre/JobOffers.fxml");
    }

    @FXML
    public void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/offre/JobOffers.fxml");
    }

    // Méthode pour naviguer vers une autre page
    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        root = FXMLLoader.load(fxmlLocation);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}

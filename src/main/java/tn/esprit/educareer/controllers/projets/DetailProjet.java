package tn.esprit.educareer.controllers.projets;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Projet;
import tn.esprit.educareer.services.ServiceProjet;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URL;

import javafx.scene.web.WebView;
import org.jsoup.Jsoup;

public class DetailProjet {

    @FXML
    private Label titreLabel;
    @FXML
    private WebView contenuLabel; // Remplacer TextArea par WebView
    @FXML
    private Label categorieLabel;
    @FXML
    private Label formateurLabel;

    private Projet projet;
    private ServiceProjet serviceProjet = new ServiceProjet();

    // Cette méthode sera appelée pour passer le projet à afficher
    public void setProjet(Projet projet) {
        this.projet = projet;
        if (projet != null) {
            titreLabel.setText("Titre: " + projet.getTitre());
            formateurLabel.setText("Formateur: " + serviceProjet.getFormateurDetailsById(projet.getFormateur_id()).getNom());

            // Convertir le contenu HTML et l'afficher dans le WebView
            String contenuHtml = projet.getContenu(); // Contenu HTML complet
            contenuLabel.getEngine().loadContent(contenuHtml); // Charger le contenu HTML dans le WebView
        }
    }

    @FXML
    public void initialize() {
        // Initialiser le projet si nécessaire
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        Parent root = FXMLLoader.load(fxmlLocation);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        navigateToPage(event, "/Projet/ListProjetClient.fxml");
    }

}

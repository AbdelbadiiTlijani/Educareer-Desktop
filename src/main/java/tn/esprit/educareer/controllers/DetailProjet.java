package tn.esprit.educareer.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.educareer.models.Projet;
import tn.esprit.educareer.services.ServiceProjet;

public class DetailProjet {

    @FXML
    private Label titreLabel;
    @FXML
    private Label contenuLabel;
    @FXML
    private Label categorieLabel;
    @FXML
    private Label formateurLabel;

    private Projet projet;

    @FXML
    public void initialize() {
        // Initialisation des informations du projet
        if (projet != null) {
            titreLabel.setText("Titre: " + projet.getTitre());
            contenuLabel.setText("Contenu: " + projet.getContenu());
            categorieLabel.setText("Catégorie ID: " + projet.getCategorie_id());
            formateurLabel.setText("Formateur ID: " + projet.getFormateur_id());
        }
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
    }

    @FXML
    private void goBack() {
        // Rediriger vers la page précédente
        // Par exemple, retourner à la vue de liste des projets
    }
}

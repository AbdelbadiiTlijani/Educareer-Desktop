package tn.esprit.educareer.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.educareer.models.Projet;
import tn.esprit.educareer.services.ServiceProjet;

public class UpdateProjet {

    @FXML
    private TextField titreField;
    @FXML
    private TextArea contenuField;
    @FXML
    private ComboBox<Integer> categorieComboBox;
    @FXML
    private TextField formateurField;

    private ServiceProjet serviceProjet = new ServiceProjet();
    private Projet projet;

    @FXML
    public void initialize() {
        // Initialisation du projet à partir de l'objet passé (par exemple via une navigation)
        // Remplir le ComboBox avec des catégories existantes
        // Par exemple, récupérer les IDs des catégories et les afficher

        // Supposons que l'objet projet a été passé via le contrôleur de la fenêtre précédente
        if (projet != null) {
            titreField.setText(projet.getTitre());
            contenuField.setText(projet.getContenu());
            formateurField.setText(String.valueOf(projet.getFormateur_id()));
            // Remplir le ComboBox avec la catégorie correspondante
            categorieComboBox.setValue(projet.getCategorie_id());
        }
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
    }

    @FXML
    private void updateProjet() {
        int categorieId = categorieComboBox.getValue();
        String titre = titreField.getText();
        String contenu = contenuField.getText();
        int formateurId = Integer.parseInt(formateurField.getText());

        projet.setCategorie_id(categorieId);
        projet.setTitre(titre);
        projet.setContenu(contenu);
        projet.setFormateur_id(formateurId);

        serviceProjet.modifier(projet);
    }
}

package tn.esprit.educareer.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.educareer.models.CategorieProjet;
import tn.esprit.educareer.services.ServiceCategorieProjet;

public class UpdateCategorieProjet {

    @FXML
    private TextField tfCategorie;

    private CategorieProjet categorie;

    ServiceCategorieProjet scp = new ServiceCategorieProjet();

    public void setCategorie(CategorieProjet cat) {
        this.categorie = cat;
        tfCategorie.setText(cat.getCategorie());
    }

    @FXML
    private void modifierCategorie() {
        String nouveauNom = tfCategorie.getText().trim();

        if (nouveauNom.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champ vide");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer un nom de catégorie.");
            alert.showAndWait();
            return;
        }

        categorie.setCategorie(nouveauNom);
        scp.modifier(categorie);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Catégorie modifiée avec succès !");
        alert.showAndWait();

        // Fermer la fenêtre
        Stage stage = (Stage) tfCategorie.getScene().getWindow();
        stage.close();
    }
}

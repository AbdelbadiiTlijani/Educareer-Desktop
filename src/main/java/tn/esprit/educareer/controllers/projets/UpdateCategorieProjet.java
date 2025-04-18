package tn.esprit.educareer.controllers.projets;

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

    private final ServiceCategorieProjet scp = new ServiceCategorieProjet();

    public void setCategorie(CategorieProjet cat) {
        this.categorie = cat;
        tfCategorie.setText(cat.getCategorie());
    }

    @FXML
    private void modifierCategorie() {
        String nouveauNom = tfCategorie.getText().trim();

        if (nouveauNom.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez entrer un nom de catégorie.");
            return;
        }

        // Mise à jour de la catégorie
        categorie.setCategorie(nouveauNom);
        scp.modifier(categorie);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie modifiée avec succès !");

        // Fermer la fenêtre après la modification
        Stage stage = (Stage) tfCategorie.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package tn.esprit.educareer.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.educareer.models.CategorieProjet;
import tn.esprit.educareer.services.ServiceCategorieProjet;

public class AddCategorieProjet {

    @FXML
    private TextField nomCategorieProjet;

    private final ServiceCategorieProjet scp = new ServiceCategorieProjet();

    @FXML
    private Button add;

    @FXML
    void ajouterCategorieProjet(ActionEvent event) {
        String nomCategorie = nomCategorieProjet.getText().trim();

        if (nomCategorie.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ requis", "Veuillez saisir un nom de catégorie.");
            return;
        }

        scp.ajouter(new CategorieProjet(nomCategorie, 1, 0));

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie ajoutée avec succès !");
        nomCategorieProjet.clear();
    }

    @FXML
    private void goToListeCategorie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReadCategoriesProjet.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) nomCategorieProjet.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

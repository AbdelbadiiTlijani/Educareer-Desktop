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
private ServiceCategorieProjet scp = new ServiceCategorieProjet();
    @FXML
    private Button add;

    @FXML
    void ajouterCategorieProjet(ActionEvent event) {
        scp.ajouter(new CategorieProjet(nomCategorieProjet.getText(), 1,0));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText("Catégorie ajoutée avec succès !");
        alert.showAndWait();
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


}

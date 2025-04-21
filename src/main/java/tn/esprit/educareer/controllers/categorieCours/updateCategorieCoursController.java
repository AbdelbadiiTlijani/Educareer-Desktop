package tn.esprit.educareer.controllers.categorieCours;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.educareer.models.CategorieCours;
import tn.esprit.educareer.services.ServiceCategorieCours;

import java.io.IOException;

public class updateCategorieCoursController {

    @FXML
    private TextField nomCategorie;

    private CategorieCours selectedCategorie;

    private final ServiceCategorieCours service = new ServiceCategorieCours();

    // Réception des données de la catégorie à modifier
    public void initData(CategorieCours categorie) {
        this.selectedCategorie = categorie;
        nomCategorie.setText(categorie.getNom());
    }

    @FXML
    void handleUpdateButton(ActionEvent event) {
        if (selectedCategorie != null) {
            selectedCategorie.setNom(nomCategorie.getText());

            service.modifier(selectedCategorie);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Catégorie mise à jour avec succès !");
            alert.showAndWait();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/categorieCours/listCategorieCours.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root , 1000 , 700));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

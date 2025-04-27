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
import java.net.URL;

public class updateCategorieCoursController {

    private Stage stage;
    private Scene scene;
    private Parent root;

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
    void handleUpdateButton(ActionEvent event) throws IOException {
        if (selectedCategorie != null) {
            selectedCategorie.setNom(nomCategorie.getText());

            service.modifier(selectedCategorie);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Catégorie mise à jour avec succès !");
            alert.showAndWait();

            navigateToPage(event, "/categorieCours/listCategorieCours.fxml");
        }
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
}

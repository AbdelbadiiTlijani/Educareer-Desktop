package tn.esprit.educareer.controllers.Reclamation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Reclamation;
import tn.esprit.educareer.models.TypeReclamation;
import tn.esprit.educareer.services.TypeReclamationService;


import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;

public class AjouterTypeReclamationController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField description;

    @FXML
    private Button handlback;

    @FXML
    private TextField nomm;

    @FXML
    private ListView<?> reclamationListView;

    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/User/AdminDashboard.fxml");

    }
    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        root = FXMLLoader.load(fxmlLocation);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root , 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    void save(ActionEvent event) throws IOException {
        String nomText = nomm.getText();
        String descriptionText = description.getText();

        if (nomText.isEmpty() || descriptionText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs requis");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }

        TypeReclamation typeReclamation = new TypeReclamation();
        typeReclamation.setNom(nomText);
        typeReclamation.setDescription(descriptionText);

        TypeReclamationService typeReclamationService = new TypeReclamationService();
        typeReclamationService.ajouter(typeReclamation);


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Type de réclamation ajouté avec succès.");
        alert.showAndWait();

        Parent root = FXMLLoader.load(getClass().getResource("/Reclamation/TypeReclamation.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }



}

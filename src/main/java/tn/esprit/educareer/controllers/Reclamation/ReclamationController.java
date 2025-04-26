package tn.esprit.educareer.controllers.Reclamation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Reclamation;
import tn.esprit.educareer.services.ReclamationService;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ReclamationController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button handlback;

    @FXML
    private ComboBox<?> idtype;

    @FXML
    private ListView<Reclamation> reclamationListView;

    @FXML
    private TextField searchField;

    private ReclamationService reclamationService = new ReclamationService();

    // Méthode pour initialiser l'affichage des réclamations
    @FXML
    public void initialize() {
        afficherReclamations();
    }

    // Méthode pour afficher toutes les réclamations
    private void afficherReclamations() {
        List<Reclamation> reclamations = reclamationService.getAll();  // Récupérer toutes les réclamations via le service
        ObservableList<Reclamation> reclamationsList = FXCollections.observableArrayList(reclamations);  // Convertir la liste en ObservableList

        reclamationListView.setItems(reclamationsList);  // Ajouter les réclamations à la ListView

        // Personnaliser l'affichage des éléments dans la ListView
        reclamationListView.setCellFactory(param -> new ListCell<Reclamation>() {
            @Override
            protected void updateItem(Reclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);
                if (empty || reclamation == null) {
                    setText(null);
                } else {
                    // Afficher le sujet, type, et la date de création dans la cellule
                    setText(reclamation.getSujet() + " - " + reclamation.getTypeReclamation().getNom() + " (crée le " + reclamation.getCreatedAt() + ")");
                }
            }
        });
    }

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
    void handleAjouterReclamation(ActionEvent event) throws IOException {
        navigateToPage(event, "/Reclamation/AjouterReclamation.fxml");
    }

    @FXML
    void handleListType(ActionEvent event) throws IOException {
        navigateToPage(event, "/Reclamation/TypeReclamation.fxml");
    }

    @FXML
    void handleAjouterType(ActionEvent event) throws IOException {
        navigateToPage(event, "/Reclamation/AjouterTypeReclamation.fxml");
    }
}

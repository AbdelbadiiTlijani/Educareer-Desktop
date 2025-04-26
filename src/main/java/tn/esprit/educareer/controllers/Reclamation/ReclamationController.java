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
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;



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
            private final Label reclamationLabel = new Label();
            private final Button supprimerBtn = new Button("Supprimer");
            private final Button updateBtn = new Button("Modifier");
            private final HBox hBox = new HBox(10, reclamationLabel, updateBtn, supprimerBtn);

            {
                // Style des boutons
                supprimerBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");
                updateBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");

                // Action pour le bouton "Supprimer"
                supprimerBtn.setOnAction(event -> {
                    Reclamation selected = getItem();
                    if (selected != null) {
                        reclamationService.supprimer(selected);  // Supprimer la réclamation via le service
                        afficherReclamations();  // Rafraîchir la liste après suppression
                    }
                });

                // Action pour le bouton "Modifier"
                updateBtn.setOnAction(event -> {
                    try {
                        // Charger la page de modification (assurez-vous que la page de modification existe)
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/ModifierReclamation.fxml"));
                        Parent root = loader.load();
                        ModifierReclamation controller = loader.getController();
                        controller.setReclamationToEdit(getItem());  // Passer la réclamation à modifier

                        Stage stage = new Stage();
                        stage.setTitle("Modifier Réclamation");
                        stage.setScene(new Scene(root));
                        stage.show();

                    } catch (IOException e) {
                        System.out.println("Erreur de navigation : " + e.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Reclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);
                if (empty || reclamation == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Mettre à jour l'affichage de chaque réclamation
                    reclamationLabel.setText(reclamation.getSujet() + " - " + reclamation.getTypeReclamation().getNom() + " (créée le " + reclamation.getCreatedAt() + ")");
                    setGraphic(hBox);  // Afficher le label et les boutons dans la cellule
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

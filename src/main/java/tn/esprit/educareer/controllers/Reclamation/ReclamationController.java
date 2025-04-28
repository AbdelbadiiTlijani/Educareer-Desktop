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
import javafx.scene.layout.VBox;

import tn.esprit.educareer.models.TypeReclamation;


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
    private ComboBox<TypeReclamation> idtype;
    @FXML
    private ListView<Reclamation> reclamationListView;

    @FXML
    private TextField searchField;
    private ReclamationService reclamationService = new ReclamationService();

    // Méthode pour initialiser l'affichage des réclamations
    @FXML
    public void initialize() {
        // Charger tous les types dans le ComboBox
        List<TypeReclamation> types = reclamationService.getAllTypes();
        ObservableList<TypeReclamation> typesList = FXCollections.observableArrayList(types);
        idtype.setItems(typesList);

        // Afficher initialement toutes les réclamations
        afficherReclamations();

        // Ajouter listener pour la barre de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAndDisplayReclamations();
        });

        // Ajouter listener pour la ComboBox de types
        idtype.setOnAction(event -> {
            filterAndDisplayReclamations();
        });
    }

    // Méthode pour tout afficher
    private void afficherReclamations() {
        List<Reclamation> reclamations = reclamationService.getAll();
        updateListView(reclamations);
    }

    // Méthode de filtrage dynamique
    private void filterAndDisplayReclamations() {
        String searchKeyword = searchField.getText().toLowerCase();
        TypeReclamation selectedType = (TypeReclamation) idtype.getSelectionModel().getSelectedItem();

        List<Reclamation> filteredList = reclamationService.getAll().stream()
                .filter(r -> (searchKeyword.isEmpty() || r.getSujet().toLowerCase().contains(searchKeyword) || r.getDescription().toLowerCase().contains(searchKeyword)))
                .filter(r -> (selectedType == null || r.getTypeReclamation().getId() == selectedType.getId()))
                .toList();

        updateListView(filteredList);
    }

    // Mise à jour du ListView avec personnalisation
    private void updateListView(List<Reclamation> reclamations) {
        ObservableList<Reclamation> reclamationsList = FXCollections.observableArrayList(reclamations);
        reclamationListView.setItems(reclamationsList);

        reclamationListView.setCellFactory(param -> new ListCell<>() {
            private final Label sujetLabel = new Label();
            private final Label descriptionLabel = new Label();
            private final Label dateLabel = new Label();
            private final Button modifierBtn = new Button("✎ Modifier");
            private final Button supprimerBtn = new Button("✖ Supprimer");
            private final HBox buttonBox = new HBox(10, modifierBtn, supprimerBtn);
            private final VBox contentBox = new VBox(5, sujetLabel, descriptionLabel, dateLabel, buttonBox);
            private final HBox rootBox = new HBox(contentBox);

            {
                rootBox.setSpacing(10);
                rootBox.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-background-radius: 10;");
                buttonBox.setStyle("-fx-alignment: center-left;");

                modifierBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
                supprimerBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");

                // Animation hover
                rootBox.setOnMouseEntered(e -> rootBox.setStyle("-fx-padding: 10; -fx-background-color: #e0e0e0; -fx-background-radius: 10;"));
                rootBox.setOnMouseExited(e -> rootBox.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-background-radius: 10;"));

                // Action boutons
                supprimerBtn.setOnAction(event -> {
                    Reclamation selected = getItem();
                    if (selected != null) {
                        reclamationService.supprimer(selected);
                        filterAndDisplayReclamations();
                    }
                });

                modifierBtn.setOnAction(event -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/ModifierReclamation.fxml"));
                        Parent root = loader.load();
                        ModifierReclamation controller = loader.getController();
                        controller.setReclamationToEdit(getItem());

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
                    sujetLabel.setText("🔹 " + reclamation.getSujet() + " (" + reclamation.getTypeReclamation().getNom() + ")");
                    descriptionLabel.setText(resumeTexte(reclamation.getDescription(), 50));
                    dateLabel.setText("🕒 Créé le : " + (reclamation.getCreatedAt() != null ? reclamation.getCreatedAt().toLocalDate() : "N/A"));
                    setGraphic(rootBox);
                }
            }
        });
    }

    // Fonction pour limiter la taille du texte description
    private String resumeTexte(String texte, int maxLength) {
        if (texte.length() <= maxLength) {
            return texte;
        }
        return texte.substring(0, maxLength) + "...";
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

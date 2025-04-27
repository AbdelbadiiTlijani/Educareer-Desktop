package tn.esprit.educareer.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Projet;
import tn.esprit.educareer.services.ServiceProjet;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ListProjetClient {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private HBox cardContainer;  // CHANGÉ de VBox à HBox

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button leftArrow;

    @FXML
    private Button rightArrow;

    private final ServiceProjet serviceProjet = new ServiceProjet();

    @FXML
    public void initialize() {
        List<Projet> projets = serviceProjet.getAll();
        displayProjects(projets);
    }

    private void displayProjects(List<Projet> projets) {
        cardContainer.getChildren().clear();
        for (Projet projet : projets) {
            VBox card = createProjectCard(projet);
            cardContainer.getChildren().add(card);
        }
    }

    private VBox createProjectCard(Projet projet) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-padding: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefSize(250, 200);

        Label title = new Label(projet.getTitre());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label category = new Label("Catégorie : " + projet.getCategorieNom());
        category.setStyle("-fx-text-fill: #666;");
        Label description = new Label("Description : " + projet.getDescription());
        description.setStyle("-fx-text-fill: black; -fx-font-weight: normal; -fx-opacity: 1.0;");

        HBox actions = new HBox(10);
        Button btnEdit = new Button("Modifier");
        btnEdit.setStyle("-fx-background-color: #00bfff; -fx-text-fill: white;");
        btnEdit.setOnAction(e -> handleEdit(projet));

        Button btnDelete = new Button("Supprimer");
        btnDelete.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
        btnDelete.setOnAction(e -> {
            serviceProjet.supprimer(projet);
            displayProjects(serviceProjet.getAll());
        });

        actions.getChildren().addAll(btnEdit, btnDelete);
        card.getChildren().addAll(title, category, description,actions);

        return card;
    }

    private void handleEdit(Projet projet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateProjet.fxml"));
            Parent root = loader.load();

            UpdateProjet controller = loader.getController();
            controller.setProjet(projet);

            Stage stage = new Stage();
            stage.setTitle("Modifier Projet");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAjoutProjet() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddProjet.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) cardContainer.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToPage(javafx.event.ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        root = FXMLLoader.load(getClass().getResource(path));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    private void handleBack(javafx.event.ActionEvent event) throws IOException {
        navigateToPage(event, "/User/FormateurDashboard.fxml");
    }

    @FXML
    private void scrollLeft() {
        scrollPane.setHvalue(scrollPane.getHvalue() - 0.2);
    }

    @FXML
    private void scrollRight() {
        scrollPane.setHvalue(scrollPane.getHvalue() + 0.2);
    }
}

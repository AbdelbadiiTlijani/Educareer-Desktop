package tn.esprit.educareer.controllers.projets;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import tn.esprit.educareer.models.Projet;
import tn.esprit.educareer.services.ServiceProjet;
import tn.esprit.educareer.utils.UserSession;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ReadProjets {

    @FXML
    private ListView<VBox> projectListView;

    private int idForma = UserSession.getInstance().getCurrentUser().getId();

    private final ServiceProjet serviceProjet = new ServiceProjet();

    @FXML
    public void initialize() {
        List<Projet> projets = serviceProjet.getAllByFormateur(idForma);
        displayProjects(projets);
    }

    private void displayProjects(List<Projet> projets) {
        projectListView.getItems().clear();
        for (Projet projet : projets) {
            VBox card = createProjectCard(projet);
            projectListView.getItems().add(card);
        }
    }

    private VBox createProjectCard(Projet projet) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-padding: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefWidth(600);

        Label title = new Label(projet.getTitre());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label category = new Label("Catégorie : " + projet.getCategorieNom());
        category.setStyle("-fx-text-fill: #666;");

        Label description = new Label("Description : " + projet.getDescription());
        description.setWrapText(true);
        description.setStyle("-fx-text-fill: black; -fx-font-weight: normal;");

        HBox actions = new HBox(10);
        Button btnEdit = new Button("Modifier");
        btnEdit.setStyle("-fx-background-color: #00bfff; -fx-text-fill: white;");
        btnEdit.setOnAction(e -> handleEdit(projet, (Node) e.getSource()));


        Button btnDelete = new Button("Supprimer");
        btnDelete.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
        btnDelete.setOnAction(e -> {
            serviceProjet.supprimer(projet);
            displayProjects(serviceProjet.getAllByFormateur(idForma));
        });

        actions.getChildren().addAll(btnEdit, btnDelete);
        card.getChildren().addAll(title, category, description, actions);

        return card;
    }

    private void handleEdit(Projet projet, Node sourceNode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Projet/UpdateProjet.fxml"));
            Parent root = loader.load();

            UpdateProjet controller = loader.getController();
            controller.setProjet(projet);

            // Close the current window
            Stage currentStage = (Stage) sourceNode.getScene().getWindow();
            currentStage.close();

            // Open new window
            Stage stage = new Stage();
            stage.setTitle("Modifier Projet");
            stage.setScene(new Scene(root , 1000 , 700));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void goToChatBox(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Projet/ChatBox.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Messagerie Privée");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAjoutProjet() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Projet/AddProjet.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1000, 700);
            Stage stage = (Stage) projectListView.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        Parent root = FXMLLoader.load(fxmlLocation);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        navigateToPage(event, "/User/FormateurDashboard.fxml");
    }
}

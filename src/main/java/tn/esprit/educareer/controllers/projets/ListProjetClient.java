package tn.esprit.educareer.controllers.projets;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import org.json.JSONObject;
import tn.esprit.educareer.models.ParticipationProjet;
import tn.esprit.educareer.models.Projet;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.server.IAImageService;
import tn.esprit.educareer.services.ServiceParticipationProjet;
import tn.esprit.educareer.services.ServiceProjet;
import tn.esprit.educareer.utils.UserSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class ListProjetClient {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private GridPane gridContainer;

    @FXML
    private ScrollPane scrollPane;

    private final ServiceProjet serviceProjet = new ServiceProjet();

    @FXML
    private void initialize() {
        List<Projet> projets = serviceProjet.getAll();

        // Listener pour rendre la grille responsive
        scrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            displayProjects(projets);
        });

        displayProjects(projets);
    }

    private void displayProjects(List<Projet> projets) {
        gridContainer.getChildren().clear();

        double width = scrollPane.getWidth();
        if (width == 0) width = 800; // Valeur par défaut si pas encore initialisé

        int cardWidth = 280; // largeur approximative d'une carte (incl. margin)
        int columnCount = Math.max(1, (int) (width / cardWidth));

        int column = 0;
        int row = 0;

        for (Projet projet : projets) {
            VBox card = createProjectCard(projet);
            gridContainer.add(card, column, row);
            column++;
            if (column == columnCount) {
                column = 0;
                row++;
            }
        }

        // Centrage horizontal
        gridContainer.setAlignment(Pos.TOP_CENTER);
    }

    private VBox createProjectCard(Projet projet) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-padding: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefSize(250, 200);

        // Créer l'ImageView pour afficher l'image du projet
        ImageView imageView = new ImageView();
        imageView.setFitWidth(230);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Charger l'image depuis l'URL de Picsum
        String imageUrl = "https://picsum.photos/200/300"; // Utilisez une URL dynamique si nécessaire
        try {
            Image image = new Image(imageUrl);
            imageView.setImage(image);
        } catch (Exception e) {
            // Gestion des erreurs si l'image ne peut pas être chargée
            System.err.println("Erreur de chargement de l'image: " + e.getMessage());
        }

        // Utilisation d'un HBox pour centrer l'image horizontalement
        HBox imageContainer = new HBox();
        imageContainer.setAlignment(Pos.CENTER);  // Centrer l'image horizontalement
        imageContainer.getChildren().add(imageView);  // Ajouter l'image dans l'HBox

        // Créer les autres éléments de la carte
        Label title = new Label(projet.getTitre());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label category = new Label("Catégorie : " + projet.getCategorieNom());
        category.setStyle("-fx-text-fill: #666;");
        Label description = new Label("Description : " + projet.getDescription());
        description.setStyle("-fx-text-fill: black; -fx-font-weight: normal;");

        User formateur = serviceProjet.getFormateurDetailsById(projet.getFormateur_id());
        Label formateurName = new Label("Formateur : " + formateur.getNom());
        Label formateurEmail = new Label("Email : " + formateur.getEmail());

        HBox actions = new HBox(10);
        Button btnOpen = new Button("Ouvrir");
        btnOpen.setStyle("-fx-background-color: #00bfff; -fx-text-fill: white;");
        btnOpen.setOnAction(e -> handleOpen(projet));

        actions.getChildren().add(btnOpen);

        // Ajouter l'HBox contenant l'image et les autres éléments à la carte
        card.getChildren().addAll(imageContainer, title, category, description, formateurName, formateurEmail, actions);

        return card;
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

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        navigateToPage(event, "/User/Student.fxml");
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

    private void handleOpen(Projet projet) {
        try {
            ServiceParticipationProjet serviceParticipationProjet = new ServiceParticipationProjet();
            ParticipationProjet participation = new ParticipationProjet();

            participation.setStudent_id(UserSession.getInstance().getCurrentUser().getId()); // <-- L'ID de l'étudiant connecté
            participation.setProjet_id(projet.getId());                 // <-- L'ID du projet cliqué
            participation.setDate_participation(Date.valueOf(LocalDate.now()).toLocalDate());

            serviceParticipationProjet.ajouter(participation);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Projet/DetailProjet.fxml"));
            Parent detailRoot = loader.load();
            DetailProjet controller = loader.getController();
            controller.setProjet(projet);
            Stage stage = (Stage) gridContainer.getScene().getWindow();
            stage.setScene(new Scene(detailRoot));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

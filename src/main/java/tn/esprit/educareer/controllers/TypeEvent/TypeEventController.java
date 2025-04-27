package tn.esprit.educareer.controllers.TypeEvent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.educareer.models.TypeEvent;
import tn.esprit.educareer.services.ServiceTypeEvent;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TypeEventController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ListView<TypeEvent> typeEventListView;

    @FXML
    private TextField searchField;

    @FXML
    private ServiceTypeEvent serviceTypeEvent;

    public TypeEventController() {
        serviceTypeEvent = new ServiceTypeEvent();
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
        scene = new Scene(root , 1000 , 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private void loadTypeEvents() {
        List<TypeEvent> typeEvents = serviceTypeEvent.getAll();
        typeEventListView.getItems().clear();
        typeEventListView.getItems().addAll(typeEvents);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadTypeEvents();

        typeEventListView.setCellFactory(param -> new ListCell<TypeEvent>() {
            @Override
            protected void updateItem(TypeEvent typeEvent, boolean empty) {
                super.updateItem(typeEvent, empty);
                if (empty || typeEvent == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox();
                    vbox.setSpacing(10);  // Ajout d'un espacement pour aérer les éléments
                    vbox.setStyle("-fx-padding: 15px; -fx-background-color: #f9f9f9; -fx-border-radius: 10px; -fx-border-color: #d3d3d3;");

                    // Nom du type d'événement
                    Label nomLabel = new Label(typeEvent.getNomE());
                    nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333;");

                    vbox.getChildren().add(nomLabel);

                    // Création de la boite d'actions (Modifier et Supprimer)
                    HBox actionButtons = new HBox();
                    actionButtons.setSpacing(10);

                    // Bouton Modifier
                    Button modifyButton = new Button("Modifier");
                    modifyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");
                    modifyButton.setOnAction(e -> handleModifier(typeEvent));

                    // Bouton Supprimer
                    Button deleteButton = new Button("Supprimer");
                    deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");
                    deleteButton.setOnAction(e -> handleSupprimer(typeEvent));

                    // Ajouter les boutons dans HBox
                    actionButtons.getChildren().addAll(modifyButton, deleteButton);
                    vbox.getChildren().add(actionButtons);

                    setGraphic(vbox);
                }
            }
        });
    }

    @FXML
    void handleAddButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TypeEvent/AddTypeEvent.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private void handleSupprimer(TypeEvent typeEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce type d'événement ?");
        alert.setContentText(typeEvent.getNomE());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                serviceTypeEvent.supprimer(typeEvent);
                loadTypeEvents(); // Actualiser la liste après suppression
            }
        });
    }


    // Méthode pour gérer la modification d'un type d'événement
    private void handleModifier(TypeEvent typeEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TypeEvent/EditTypeEvent.fxml"));
            Parent root = loader.load();
            EditTypeEventController controller = loader.getController();
            controller.setTypeEvent(typeEvent);  // Passer le type d'événement à modifier

            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

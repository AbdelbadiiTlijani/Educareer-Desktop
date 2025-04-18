package tn.esprit.educareer.controllers.Event;

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
import tn.esprit.educareer.models.Event;
import tn.esprit.educareer.services.ServiceEvent;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EventController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ListView<Event> eventListView;

    @FXML
    private TextField searchField;

    private ServiceEvent serviceEvent;

    public EventController() {
        serviceEvent = new ServiceEvent();
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
        scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private void loadEvents() {
        List<Event> events = serviceEvent.getAll();
        eventListView.getItems().clear();
        eventListView.getItems().addAll(events);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadEvents();

        eventListView.setCellFactory(param -> new ListCell<Event>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox();
                    vbox.setSpacing(5);
                    vbox.setStyle("-fx-border-color: #d3d3d3; -fx-padding: 10px; -fx-background-color: #f9f9f9;");

                    Label titreLabel = new Label(event.getTitre());
                    titreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    Label dateLabel = new Label("Date: " + event.getDate().toString());
                    dateLabel.setStyle("-fx-font-size: 12px;");

                    Label lieuLabel = new Label("Lieu: " + event.getLieu());
                    lieuLabel.setStyle("-fx-font-size: 12px;");

                    Label nbrPlacesLabel = new Label("Places disponibles: " + event.getNbrPlace());
                    nbrPlacesLabel.setStyle("-fx-font-size: 12px;");

                    vbox.getChildren().addAll(titreLabel, dateLabel, lieuLabel, nbrPlacesLabel);

                    HBox actionButtons = new HBox();
                    actionButtons.setSpacing(10);

                    Button modifyButton = new Button("Modifier");
                    modifyButton.setOnAction(e -> handleModifier(event));

                    Button deleteButton = new Button("Supprimer");
                    deleteButton.setOnAction(e -> handleSupprimer(event));

                    actionButtons.getChildren().addAll(modifyButton, deleteButton);
                    vbox.getChildren().add(actionButtons);

                    setGraphic(vbox);
                }
            }
        });
    }

    private void handleModifier(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event/EditEvent.fxml"));
            Parent root = loader.load();

            EditEventController editEventController = loader.getController();
            editEventController.setEvent(event);

            Stage stage = (Stage) eventListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSupprimer(Event event) {
        serviceEvent.supprimer(event);
        loadEvents();
    }
}

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
                    vbox.setSpacing(5);
                    vbox.setStyle("-fx-border-color: #d3d3d3; -fx-padding: 10px; -fx-background-color: #f9f9f9;");

                    Label nomLabel = new Label("Nom Type: " + typeEvent.getNomE());
                    nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    vbox.getChildren().add(nomLabel);

                    HBox actionButtons = new HBox();
                    actionButtons.setSpacing(10);

                    Button deleteButton = new Button("Supprimer");
                    deleteButton.setOnAction(e -> handleSupprimer(typeEvent));

                    actionButtons.getChildren().addAll(deleteButton);
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
// Set up the scene with a larger aspect ratio
        Scene scene = new Scene(root, 1000, 700);        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }




    private void handleSupprimer(TypeEvent typeEvent) {
        serviceTypeEvent.supprimer(typeEvent);
        loadTypeEvents();
    }
}

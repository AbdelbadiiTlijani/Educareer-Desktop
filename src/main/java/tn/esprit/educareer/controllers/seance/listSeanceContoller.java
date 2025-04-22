package tn.esprit.educareer.controllers.seance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Seance;
import tn.esprit.educareer.services.ServiceSeance;

import java.io.IOException;
import java.net.URL;

public class listSeanceContoller {
    private final ServiceSeance serviceSeance = new ServiceSeance();

    @FXML
    private ComboBox<Seance> seanceFilter;

    @FXML
    private Label jomlaa;

    @FXML
    private ListView<Seance> seanceListView;

    @FXML
    private TextField searchField;

    @FXML
    private Label titre;

    @FXML
    private Button viewBackButton;


    private Stage stage;
    private Scene scene;

    @FXML
    public void initialize() {
        ObservableList<Seance> observableList = FXCollections.observableArrayList(serviceSeance.getAll());
        seanceListView.setItems(observableList);

        seanceListView.setCellFactory(listView -> new ListCell<>() {
            private final Label nomLabel = new Label();
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            private final HBox hbox = new HBox(10, nomLabel, editButton, deleteButton);

            {
                editButton.getStyleClass().add("btn-modifier");
                deleteButton.getStyleClass().add("btn-supprimer");
                editButton.setOnAction(this::handleEdit);
                deleteButton.setOnAction(this::handleDelete);
            }

            @Override
            protected void updateItem(Seance seance, boolean empty) {
                super.updateItem(seance, empty);
                if (empty || seance == null) {
                    setGraphic(null);
                } else {
                    nomLabel.setText(seance.getTitre());
                    nomLabel.setStyle("-fx-font-family: bold; -fx-font-size: 20px;-fx-text-fill: #2c3e50;");
                    setGraphic(hbox);
                }
            }

            private void handleEdit(ActionEvent event) {
                Seance selected = getItem();
                if (selected != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/seance/updateSeance.fxml"));
                        Parent root = loader.load();

                        updateSeanceController controller = loader.getController();
                        controller.initData(selected);

                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void handleDelete(ActionEvent event) {
                Seance selected = getItem();
                if (selected != null) {
                    serviceSeance.supprimer(selected);
                    getListView().getItems().remove(selected);
                }
            }
        });
    }

    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/User/FormateurDashboard.fxml");
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

}

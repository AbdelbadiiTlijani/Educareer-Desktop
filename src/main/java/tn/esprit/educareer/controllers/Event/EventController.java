package tn.esprit.educareer.controllers.Event;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Event;
import tn.esprit.educareer.services.ServiceEvent;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
        scene = new Scene(root , 1000 , 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private void loadEvents(List<Event> events) {
        eventListView.getItems().clear();
        eventListView.getItems().addAll(events);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Charger tous les événements par défaut
        loadEvents(serviceEvent.getAll());

        // Ajouter un listener pour détecter les changements dans le champ de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEvents(newValue);
        });

        // Personnaliser l'affichage des événements dans la ListView
        eventListView.setCellFactory(param -> new ListCell<Event>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox();
                    vbox.setSpacing(10);
                    vbox.setStyle("-fx-padding: 15px; -fx-background-color: #f9f9f9; -fx-border-radius: 10px; -fx-border-color: #d3d3d3;");

                    Label titreLabel = new Label(event.getTitre());
                    titreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333;");

                    Label dateLabel = new Label("Date: " + event.getDate().toString());
                    dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

                    Label lieuLabel = new Label("Lieu: " + event.getLieu());
                    lieuLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

                    Label nbrPlacesLabel = new Label("Places disponibles: " + event.getNbrPlace());
                    nbrPlacesLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

                    vbox.getChildren().addAll(titreLabel, dateLabel, lieuLabel, nbrPlacesLabel);

                    HBox actionButtons = new HBox();
                    actionButtons.setSpacing(10);

                    Button modifyButton = new Button("Modifier");
                    modifyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");
                    modifyButton.setOnAction(e -> handleModifier(event));

                    Button deleteButton = new Button("Supprimer");
                    deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");
                    deleteButton.setOnAction(e -> handleSupprimer(event));

                    Button qrButton = new Button("QR Code");
                    qrButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");
                    qrButton.setOnAction(e -> showQRCodePopup(event));

                    actionButtons.getChildren().addAll(modifyButton, deleteButton, qrButton);
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

            Scene scene = new Scene(root, 1000, 700);

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSupprimer(Event event) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de Suppression");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet événement ?");
        confirmationAlert.setContentText("Cette action est irréversible.");

        ButtonType response = confirmationAlert.showAndWait().orElse(ButtonType.CANCEL);

        if (response == ButtonType.OK) {
            serviceEvent.supprimer(event);
            loadEvents(serviceEvent.getAll());  // Recharger les événements après suppression
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Suppression réussie");
            successAlert.setHeaderText("Événement supprimé");
            successAlert.setContentText("L'événement a été supprimé avec succès.");
            successAlert.showAndWait();
        }
    }

    private void showQRCodePopup(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event/QRCodePopup.fxml"));
            Parent root = loader.load();

            QRCodePopupController controller = loader.getController();

            // Tu peux choisir ce que tu veux encoder ici, exemple : l’ID ou le titre
            String qrData = "Event ID: " + event.getId() + "\nTitre: " + event.getTitre() + "\nLieu: " + event.getLieu() + "\nNombres de places disponibles: " + event.getNbrPlace();
            controller.setQRCodeData(qrData);

            Stage stage = new Stage();
            stage.setTitle("QR Code de l'événement");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void filterEvents(String query) {
        List<Event> allEvents = serviceEvent.getAll();
        List<Event> filteredEvents = allEvents.stream()
                .filter(event -> event.getTitre().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        loadEvents(filteredEvents);
    }
}

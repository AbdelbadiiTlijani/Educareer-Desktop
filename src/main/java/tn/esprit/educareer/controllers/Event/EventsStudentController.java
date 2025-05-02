// EventsStudentController.java
package tn.esprit.educareer.controllers.Event;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Event;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ServiceEvent;
import tn.esprit.educareer.services.ServiceParticipation;

import javafx.event.ActionEvent;
import java.io.IOException;

public class EventsStudentController {

    @FXML
    private GridPane eventsGrid;

    private ServiceEvent eventService;
    private ServiceParticipation participationService;
    private User currentStudent;

    @FXML
    public void initialize() {
        eventService = new ServiceEvent();
        participationService = new ServiceParticipation();

        currentStudent = new User();
        currentStudent.setId(1);

        ObservableList<Event> events = FXCollections.observableArrayList(eventService.getAll());

        int column = 0;
        int row = 0;

        for (Event event : events) {
            AnchorPane card = createEventCard(event);
            eventsGrid.add(card, column, row);

            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    private AnchorPane createEventCard(Event event) {
        AnchorPane card = new AnchorPane();
        card.setPrefSize(250, 180);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-border-radius: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");

        VBox content = new VBox(5);
        content.setPadding(new Insets(10));

        Label title = new Label(event.getTitre());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4e54c8;");

        Label description = new Label(event.getDescription());
        description.setStyle("-fx-font-size: 13px; -fx-text-fill: #333;");
        description.setWrapText(true);

        Label location = new Label("\uD83D\uDCCD " + event.getLieu());
        location.setStyle("-fx-text-fill: #666;");

        Label date = new Label("\uD83D\uDCC5 " + event.getDate().toString());
        date.setStyle("-fx-text-fill: #666;");

        Label seats = new Label("\uD83E\uDE91 " + event.getNbrPlace() + " places");
        seats.setStyle("-fx-text-fill: #666;");

        Button participateButton = new Button("Participer");
        participateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 10px; -fx-cursor: hand;");
        participateButton.setOnAction(e -> handleParticipateButton(event, seats));

        content.getChildren().addAll(title, description, location, date, seats, participateButton);
        card.getChildren().add(content);

        return card;
    }

    private void handleParticipateButton(Event event, Label availableSeatsLabel) {
        int studentId = currentStudent.getId();
        int eventId = event.getId();

        if (participationService.isParticipated(eventId, studentId)) {
            showAlert(Alert.AlertType.WARNING, "Déjà inscrit", "Vous êtes déjà inscrit à cet événement.");
        } else if (event.getNbrPlace() > 0) {
            participationService.participate(eventId, studentId);
            event.setNbrPlace(event.getNbrPlace() - 1);
            eventService.modifier(event);
            availableSeatsLabel.setText("\uD83E\uDE91 " + event.getNbrPlace() + " places");
            showAlert(Alert.AlertType.INFORMATION, "Inscription réussie", "Vous êtes inscrit à l'événement: " + event.getTitre());
        } else {
            showAlert(Alert.AlertType.WARNING, "Complet", "Désolé, cet événement est complet.");
        }
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/student.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

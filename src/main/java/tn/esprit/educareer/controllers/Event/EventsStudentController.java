package tn.esprit.educareer.controllers.Event;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import tn.esprit.educareer.models.Event;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ServiceEvent;
import tn.esprit.educareer.services.ServiceParticipation;

public class EventsStudentController {

    @FXML
    private ListView<Event> eventsListView; // ListView for displaying the events

    private ServiceEvent eventService;
    private ServiceParticipation participationService;

    private User currentStudent; // Utilisateur connecté

    @FXML
    public void initialize() {
        eventService = new ServiceEvent();
        participationService = new ServiceParticipation();

        // Simulation d'un étudiant connecté (remplacer par la vraie récupération de l'utilisateur connecté)
        currentStudent = new User();
        currentStudent.setId(1); // Exemple : ID de l'étudiant connecté est 1

        // Fetch the list of events from the database
        ObservableList<Event> events = FXCollections.observableArrayList(eventService.getAll());

        // Set the ListView's items to the fetched events
        eventsListView.setItems(events);

        // Customize how each event is displayed in the ListView
        eventsListView.setCellFactory(param -> new ListCell<Event>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(createEventCard(event));
                }
            }
        });
    }

    private HBox createEventCard(Event event) {
        HBox eventCard = new HBox(10);
        eventCard.getStyleClass().add("event-card");

        Label title = new Label(event.getTitre());
        title.getStyleClass().add("event-title");

        Label description = new Label(event.getDescription());
        description.getStyleClass().add("event-info");

        Label location = new Label("Lieu: " + event.getLieu());
        location.getStyleClass().add("event-location");

        Label date = new Label("Date: " + event.getDate().toString());
        date.getStyleClass().add("event-date");

        Label availableSeats = new Label("Places disponibles: " + event.getNbrPlace());
        availableSeats.getStyleClass().add("event-info");

        Button participateButton = new Button("Participer");
        participateButton.getStyleClass().add("participate-button");

        participateButton.setOnAction(e -> handleParticipateButton(event, availableSeats));

        eventCard.getChildren().addAll(title, description, location, date, availableSeats, participateButton);

        return eventCard;
    }

    private void handleParticipateButton(Event event, Label availableSeatsLabel) {
        int studentId = currentStudent.getId(); // ID de l'étudiant connecté
        int eventId = event.getId(); // ID de l'événement

        if (participationService.isParticipated(eventId, studentId)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Déjà inscrit");
            alert.setHeaderText("Vous êtes déjà inscrit !");
            alert.setContentText("Vous avez déjà participé à cet événement.");
            alert.showAndWait();
        } else {
            if (event.getNbrPlace() > 0) {
                // Participer
                participationService.participate(eventId, studentId);

                // Diminuer les places disponibles
                event.setNbrPlace(event.getNbrPlace() - 1);

                // Mettre à jour l'événement dans la base
                eventService.modifier(event);

                // Mise à jour de l'affichage immédiate
                availableSeatsLabel.setText("Places disponibles: " + event.getNbrPlace());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Inscription réussie");
                alert.setHeaderText("Vous êtes inscrit !");
                alert.setContentText("Vous participez à l'événement : " + event.getTitre());
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Complet");
                alert.setHeaderText("Plus de places disponibles !");
                alert.setContentText("Désolé, cet événement est complet.");
                alert.showAndWait();
            }
        }
    }
}

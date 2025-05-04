package tn.esprit.educareer.controllers.Event;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Event;

public class EventDetailsController {

    @FXML private Label titleLabel;
    @FXML private Label typeLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label dateLabel;
    @FXML private Label locationLabel;
    @FXML private Label placesLabel;
    @FXML private Button closeButton;

    private Stage stage;

    @FXML
    public void initialize() {
        closeButton.setOnAction(e -> stage.close());
    }

    public void setEvent(Event event) {
        titleLabel.setText(event.getTitre());
        typeLabel.setText("Type : " + event.getTypeEvent().getNomE());
        descriptionLabel.setText("Description : " + event.getDescription());
        dateLabel.setText("Date : " + event.getDate().toString());
        locationLabel.setText("Lieu : " + event.getLieu());
        placesLabel.setText("Places disponibles : " + event.getNbrPlace());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

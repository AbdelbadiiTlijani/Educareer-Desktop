package tn.esprit.educareer.controllers.Event;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.educareer.models.Event;
import tn.esprit.educareer.services.ServiceEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class CalendarController {

    @FXML private GridPane calendarGrid;
    @FXML private Label monthYearLabel;
    @FXML private Button prevMonthButton;
    @FXML private Button nextMonthButton;
    @FXML private Button returnButton; // Nouveau bouton retour

    private YearMonth currentYearMonth;
    private final ServiceEvent serviceEvent = new ServiceEvent();

    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        buildCalendar();

        prevMonthButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            buildCalendar();
        });

        nextMonthButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            buildCalendar();
        });

        returnButton.setOnAction(e -> { // Action bouton retour
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/AdminDashboard.fxml")); //
                Parent dashboardRoot = loader.load();

                Stage currentStage = (Stage) returnButton.getScene().getWindow();
                currentStage.setScene(new Scene(dashboardRoot));
                //currentStage.setTitle("Dashboard");

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void buildCalendar() {
        calendarGrid.getChildren().clear();

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), calendarGrid);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        List<Event> events = serviceEvent.getAll();

        monthYearLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
        int daysInMonth = currentYearMonth.lengthOfMonth();

        int col = (dayOfWeek - 1) % 7;
        int row = 0;

        for (int day = 1; day <= daysInMonth; day++) {
            VBox dayCell = new VBox();
            dayCell.getStyleClass().add("day-cell");

            Label dayNumber = new Label(String.valueOf(day));
            dayNumber.getStyleClass().add("day-number");
            dayCell.getChildren().add(dayNumber);

            LocalDate currentDate = currentYearMonth.atDay(day);

            for (Event event : events) {
                if (event.getDate().toLocalDate().isEqual(currentDate)) {
                    Label eventLabel = new Label(event.getTitre() + " (" + event.getDate().toLocalTime() + ")");
                    eventLabel.getStyleClass().add("event-label");
                    eventLabel.setOnMouseClicked(e -> showEventDetails(event));
                    dayCell.getChildren().add(eventLabel);
                }
            }

            calendarGrid.add(dayCell, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private void showEventDetails(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event/EventDetailsView.fxml"));
            Parent root = loader.load();

            EventDetailsController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            controller.setEvent(event);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/Event/event-details.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Détails de l'événement");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

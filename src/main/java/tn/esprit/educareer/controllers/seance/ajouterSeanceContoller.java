package tn.esprit.educareer.controllers.seance;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.educareer.models.Cours;
import tn.esprit.educareer.models.Holiday;
import tn.esprit.educareer.models.Seance;
import tn.esprit.educareer.services.ServiceCours;
import tn.esprit.educareer.services.ServiceSeance;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class ajouterSeanceContoller {
    public static final String CALENDARIFIC_API_KEY = "U5OoElWmHW7xJeCeepAssfkP9AY9G1GN";

    private final String errorStyle = "-fx-border-color: red; -fx-border-width: 2px;";
    private final String originalStyle = "";
    private final ServiceSeance serviceSeance = new ServiceSeance();
    private final ServiceCours serviceCours = new ServiceCours();
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField titreSeance, descriptionSeance, dureeSeance;
    @FXML
    private ComboBox<Cours> coursComboBox;
    @FXML
    private Label titreErrorLabel, descriptionErrorLabel, dateErrorLabel, dureeErrorLabel, coursErrorLabel, globalErrorLabel;
    @FXML
    private DatePicker dateSeance;
    @FXML
    private Button viewAddSeanceButton;
    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // Remplissage de la ComboBox avec les cours
        coursComboBox.getItems().addAll(serviceCours.getAll());
        coursComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Cours item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
        coursComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Cours item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });

        // Rendre le champ durée numérique uniquement
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        dureeSeance.setTextFormatter(textFormatter);

        // Action du bouton "Retour"
        backButton.setOnAction(e -> goBack());
        Locale.setDefault(new Locale("fr", "TN"));
      //  List<Holiday> listHolidays = null;

      List<Holiday> listHolidays = CalendarificAPI.getHolidays(CALENDARIFIC_API_KEY, Locale.getDefault().getCountry(), String.valueOf(LocalDate.now().getYear()));


        if (listHolidays != null && !listHolidays.isEmpty()) {
            dateSeance.setDayCellFactory(new Callback<>() {
                @Override
                public DateCell call(DatePicker datePicker) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate date, boolean empty) {
                            super.updateItem(date, empty);
                            LocalDate today = LocalDate.now();
                            Optional<Holiday> holiday = listHolidays.stream().filter(ho -> ho.getDate().getIso().equals(date.toString())).findAny();
                            // If the date is a holiday, set a tooltip
                            if (date != null && !empty && holiday.isPresent()) {
                                if (date.compareTo(today) < 0) {
                                    setDisable(true);
                                }
                                setStyle("-fx-background-color: #ffd639;");
                                setTooltip(new Tooltip(holiday.get().getName()));
                            } else {
                                setStyle("");
                                setDisable(empty || date.compareTo(today) < 0);

                                if (date.compareTo(today) < 0) {
                                    setStyle("-fx-background-color: #ffc0cb;");
                                    setDisable(true);
                                }
                            }
                        }
                    };
                }
            });
        }
    }


    @FXML
    void handleAddSeanceButton(ActionEvent event) throws IOException {
        resetErrorMessages();

        boolean isValid = true;
        String titre = titreSeance.getText().trim();
        String description = descriptionSeance.getText().trim();
        Date dateTime = null;
        if (dateSeance.getValue() != null) {
            dateTime = Date.from(dateSeance.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        String dureeStr = dureeSeance.getText().trim();
        Cours cours = coursComboBox.getValue();

        if (titre.isEmpty()) {
            titreErrorLabel.setText("Le titre est requis.");
            titreSeance.setStyle(errorStyle);
            isValid = false;
        }

        if (description.isEmpty()) {
            descriptionErrorLabel.setText("La description est requise.");
            descriptionSeance.setStyle(errorStyle);
            isValid = false;
        } else if (description.length() < 2) {
            descriptionErrorLabel.setText("La description doit contenir au moins 2 caractères.");
            descriptionSeance.setStyle(errorStyle);
            isValid = false;
        }

        if (dateSeance.getValue() != null && dateTime.before(new Date())) {
            dateErrorLabel.setText("La date doit être dans le futur.");
            dateSeance.setStyle(errorStyle);
            isValid = false;
        }

        int duree = 0;
        if (dureeStr.isEmpty()) {
            dureeErrorLabel.setText("La durée est requise.");
            dureeSeance.setStyle(errorStyle);
            isValid = false;
        } else if (!dureeStr.matches("\\d+")) {
            dureeErrorLabel.setText("La durée doit être un nombre valide.");
            dureeSeance.setStyle(errorStyle);
            isValid = false;
        } else {
            duree = Integer.parseInt(dureeStr);
            if (duree <= 0) {
                dureeErrorLabel.setText("La durée doit être supérieure à 0.");
                dureeSeance.setStyle(errorStyle);
                isValid = false;
            }
        }

        if (cours == null) {
            coursErrorLabel.setText("Veuillez choisir un cours.");
            coursComboBox.setStyle(errorStyle);
            isValid = false;
        }

        if (!isValid) {
            globalErrorLabel.setText("Veuillez corriger les erreurs.");
            globalErrorLabel.setStyle("-fx-text-fill: red;");
            globalErrorLabel.setVisible(true);
            return;
        }

        Seance seance = new Seance(titre, description, dateTime, duree, cours);
        serviceSeance.ajouter(seance);

        globalErrorLabel.setStyle("-fx-text-fill: #10b981;");
        globalErrorLabel.setText("Séance ajoutée avec succès !");
        globalErrorLabel.setVisible(true);

        // Réinitialiser les champs
        titreSeance.clear();
        descriptionSeance.clear();
        dateSeance.setValue(null);
        dureeSeance.clear();
        coursComboBox.getSelectionModel().clearSelection();

        navigateToPage(event, "/seance/listSeance.fxml");
    }

    private void resetErrorMessages() {
        titreErrorLabel.setText("");
        descriptionErrorLabel.setText("");
        dateErrorLabel.setText("");
        dureeErrorLabel.setText("");
        coursErrorLabel.setText("");

        titreSeance.setStyle(originalStyle);
        descriptionSeance.setStyle(originalStyle);
        dateSeance.setStyle(originalStyle);
        dureeSeance.setStyle(originalStyle);
        coursComboBox.setStyle(originalStyle);

        globalErrorLabel.setText("");
        globalErrorLabel.setVisible(false);
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

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/FormateurDashboard.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 1000, 700);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

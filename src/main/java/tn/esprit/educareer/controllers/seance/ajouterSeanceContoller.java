package tn.esprit.educareer.controllers.seance;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Cours;
import tn.esprit.educareer.models.Seance;
import tn.esprit.educareer.services.ServiceCours;
import tn.esprit.educareer.services.ServiceSeance;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ajouterSeanceContoller {

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
    private DatePicker dateSeance;  // Remplacer TextField par DatePicker

    private final ServiceSeance serviceSeance = new ServiceSeance();
    private final ServiceCours serviceCours = new ServiceCours();

    @FXML
    public void initialize() {
        List<Cours> coursList = serviceCours.getAll();
        coursComboBox.getItems().addAll(coursList);
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

    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/User/AdminDashboard.fxml");
    }

    @FXML
    void ajoutSeance(ActionEvent event) throws IOException {
        boolean isValid = true;
        clearErrorLabels();

        String titre = titreSeance.getText().trim();
        String description = descriptionSeance.getText().trim();
        LocalDate date = dateSeance.getValue();  // Récupération de la date choisie
        String dureeStr = dureeSeance.getText().trim();
        Cours selectedCours = coursComboBox.getValue();

        if (titre.isEmpty()) {
            titreErrorLabel.setText("Le titre est requis.");
            isValid = false;
        }

        if (description.isEmpty()) {
            descriptionErrorLabel.setText("La description est requise.");
            isValid = false;
        }

        LocalDateTime dateTime = null;
        if (date != null) {
            dateTime = date.atStartOfDay();  // Conversion de la date en LocalDateTime
        } else {
            dateErrorLabel.setText("La date est requise.");
            isValid = false;
        }

        int duree = 0;
        try {
            duree = Integer.parseInt(dureeStr);
            if (duree <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            dureeErrorLabel.setText("Durée invalide.");
            isValid = false;
        }

        if (selectedCours == null) {
            coursErrorLabel.setText("Veuillez choisir un cours.");
            isValid = false;
        }

        if (!isValid) {
            globalErrorLabel.setText("Veuillez corriger les erreurs.");
            globalErrorLabel.setVisible(true);
            return;
        }

        Seance seance = new Seance(titre, description, dateTime, duree, selectedCours);
        serviceSeance.ajouter(seance);

        globalErrorLabel.setStyle("-fx-text-fill: #10b981;");
        globalErrorLabel.setText("Séance ajoutée avec succès !");
        globalErrorLabel.setVisible(true);
        clearForm();
        navigateToPage(event, "/seance/listSeance.fxml");
    }

    private void clearErrorLabels() {
        titreErrorLabel.setText("");
        descriptionErrorLabel.setText("");
        dateErrorLabel.setText("");
        dureeErrorLabel.setText("");
        coursErrorLabel.setText("");
        globalErrorLabel.setVisible(false);
    }

    private void clearForm() {
        titreSeance.clear();
        descriptionSeance.clear();
        dateSeance.setValue(null);  // Réinitialiser la DatePicker
        dureeSeance.clear();
        coursComboBox.setValue(null);
    }
}

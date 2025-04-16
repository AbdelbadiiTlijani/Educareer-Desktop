package tn.esprit.educareer.controllers.Event;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Event;
import tn.esprit.educareer.models.TypeEvent;
import tn.esprit.educareer.services.ServiceEvent;
import tn.esprit.educareer.services.ServiceTypeEvent;

import java.io.IOException;

public class AddEventController {

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField lieuField;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField nbrPlacesField;

    @FXML
    private TextField titreField;

    @FXML
    private ComboBox<TypeEvent> typeComboBox;

    private ServiceTypeEvent serviceTypeEvent;
    private ServiceEvent serviceEvent;

    // Constructeur
    public AddEventController() {
        serviceTypeEvent = new ServiceTypeEvent();
        serviceEvent = new ServiceEvent();
    }

    @FXML
    public void initialize() {
        if (typeComboBox != null) {
            typeComboBox.getItems().addAll(serviceTypeEvent.getAll());
        }
    }

    @FXML
    public void ajouter(ActionEvent event) {
        StringBuilder errorMessage = new StringBuilder();

        if (titreField.getText().isEmpty()) errorMessage.append("Le titre est obligatoire.\n");
        if (descriptionField.getText().isEmpty()) errorMessage.append("La description est obligatoire.\n");
        if (lieuField.getText().isEmpty()) errorMessage.append("Le lieu est obligatoire.\n");

        if (nbrPlacesField.getText().isEmpty()) {
            errorMessage.append("Le nombre de places est obligatoire.\n");
        } else {
            try {
                Integer.parseInt(nbrPlacesField.getText());
            } catch (NumberFormatException e) {
                errorMessage.append("Le nombre de places doit être un nombre entier valide.\n");
            }
        }

        TypeEvent selectedTypeEvent = typeComboBox.getValue();
        if (selectedTypeEvent == null) {
            errorMessage.append("Le type d'événement est obligatoire.\n");
        }

        if (dateField.getValue() == null) {
            errorMessage.append("La date est obligatoire.\n");
        } else if (dateField.getValue().isBefore(java.time.LocalDate.now())) {
            errorMessage.append("La date ne peut pas être dans le passé.\n");
        }

        if (errorMessage.length() > 0) {
            messageLabel.setText(errorMessage.toString());
            messageLabel.setStyle("-fx-text-fill: red;");
        } else {
            Event eventToAdd = new Event();
            eventToAdd.setTitre(titreField.getText());
            eventToAdd.setDescription(descriptionField.getText());
            eventToAdd.setLieu(lieuField.getText());
            eventToAdd.setNbrPlace(Integer.parseInt(nbrPlacesField.getText()));
            eventToAdd.setDate(dateField.getValue());
            eventToAdd.setTypeEvent(selectedTypeEvent);

            serviceEvent.ajouter(eventToAdd);

            messageLabel.setText("Événement ajouté avec succès !");
            messageLabel.setStyle("-fx-text-fill: green;");

            clearFields();
        }
    }

    @FXML
    public void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/AdminDashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        titreField.clear();
        descriptionField.clear();
        lieuField.clear();
        nbrPlacesField.clear();
        typeComboBox.setValue(null);
        dateField.setValue(null);
    }
}

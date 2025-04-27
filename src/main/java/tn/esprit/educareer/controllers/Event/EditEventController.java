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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EditEventController {

    @FXML
    private TextField titreField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField lieuField;

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField heureField; // Champ pour l'heure

    @FXML
    private TextField nbrPlacesField;

    @FXML
    private ComboBox<TypeEvent> typeComboBox;

    @FXML
    private Label messageLabel;

    private Event event;
    private ServiceEvent serviceEvent;
    private ServiceTypeEvent serviceTypeEvent;

    // Constructeur
    public EditEventController() {
        serviceEvent = new ServiceEvent();
        serviceTypeEvent = new ServiceTypeEvent();
    }

    public void setEvent(Event event) {
        this.event = event;

        // Charger les types dans la ComboBox
        typeComboBox.getItems().addAll(serviceTypeEvent.getAll());

        // Pré-sélectionner le type de l'événement
        typeComboBox.setValue(event.getTypeEvent());

        // Remplir les autres champs
        titreField.setText(event.getTitre());
        descriptionField.setText(event.getDescription());
        lieuField.setText(event.getLieu());
        dateField.setValue(event.getDate().toLocalDate());
        heureField.setText(event.getDate().toLocalTime().toString()); // "HH:mm"
        nbrPlacesField.setText(String.valueOf(event.getNbrPlace()));
    }

    @FXML
    public void handleModifier(ActionEvent actionEvent) {
        StringBuilder errorMessage = new StringBuilder();

        if (titreField.getText().isEmpty()) {
            errorMessage.append("Le titre est obligatoire.\n");
        }
        if (descriptionField.getText().isEmpty()) {
            errorMessage.append("La description est obligatoire.\n");
        }
        if (lieuField.getText().isEmpty()) {
            errorMessage.append("Le lieu est obligatoire.\n");
        }
        if (nbrPlacesField.getText().isEmpty()) {
            errorMessage.append("Le nombre de places est obligatoire.\n");
        } else {
            try {
                int nbrPlaces = Integer.parseInt(nbrPlacesField.getText());
                if (nbrPlaces < 0) {
                    errorMessage.append("Le nombre de places doit être un entier naturel (positif ou zéro).\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Le nombre de places doit être un nombre entier valide.\n");
            }
        }

        if (dateField.getValue() == null) {
            errorMessage.append("La date est obligatoire.\n");
        }

        if (heureField.getText().isEmpty()) {
            errorMessage.append("L'heure est obligatoire.\n");
        } else {
            try {
                LocalTime.parse(heureField.getText()); // Validation de l'heure
            } catch (Exception e) {
                errorMessage.append("L'heure doit être au format HH:mm.\n");
            }
        }

        if (typeComboBox.getValue() == null) {
            errorMessage.append("Le type d'événement est obligatoire.\n");
        }

        if (errorMessage.length() > 0) {
            messageLabel.setText(errorMessage.toString());
            messageLabel.setStyle("-fx-text-fill: red;");
        } else {
            // Mise à jour de l'événement
            LocalDate date = dateField.getValue();
            LocalTime time = LocalTime.parse(heureField.getText());
            LocalDateTime dateTime = LocalDateTime.of(date, time);

            event.setTitre(titreField.getText());
            event.setDescription(descriptionField.getText());
            event.setLieu(lieuField.getText());
            event.setNbrPlace(Integer.parseInt(nbrPlacesField.getText()));
            event.setDate(dateTime);
            event.setTypeEvent(typeComboBox.getValue());

            // Appel au service pour enregistrer les modifications
            serviceEvent.modifier(event);

            messageLabel.setText("Événement modifié avec succès !");
            messageLabel.setStyle("-fx-text-fill: green;");
        }
    }

    @FXML
    public void handleAnnuler(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de la liste des événements
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event/EventList.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Scene currentScene = ((Node) actionEvent.getSource()).getScene();

            // Remplacer le contenu de la scène actuelle par la scène de la liste des événements
            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(new Scene(root));

            // Optionnel : Afficher à nouveau la scène

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Gestion d'erreur si le fichier FXML de la liste des événements n'est pas trouvé
            messageLabel.setText("Erreur de chargement de la liste des événements.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }

    }
}

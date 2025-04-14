package tn.esprit.educareer.controllers.Event;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Event;
import tn.esprit.educareer.models.TypeEvent;
import tn.esprit.educareer.services.ServiceEvent;
import tn.esprit.educareer.services.ServiceTypeEvent;

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
    private TextField nbrPlacesField;

    @FXML
    private ComboBox<TypeEvent> typeComboBox;

    @FXML
    private Label messageLabel;  // Pour afficher les messages de validation

    private Event event;
    private ServiceEvent serviceEvent;
    private ServiceTypeEvent serviceTypeEvent;

    // Constructeur
    public EditEventController() {
        serviceEvent = new ServiceEvent();
        serviceTypeEvent = new ServiceTypeEvent();
    }

    // Cette méthode est appelée pour initialiser l'événement à modifier
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
        dateField.setValue(event.getDate());
        nbrPlacesField.setText(String.valueOf(event.getNbrPlace()));
    }

    // Méthode pour enregistrer les modifications de l'événement
    @FXML
    public void handleModifier(ActionEvent actionEvent) {
        StringBuilder errorMessage = new StringBuilder();

        // Vérification des champs de saisie
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
                Integer.parseInt(nbrPlacesField.getText());  // Vérifie si c'est un nombre entier valide
            } catch (NumberFormatException e) {
                errorMessage.append("Le nombre de places doit être un nombre entier valide.\n");
            }
        }
        if (dateField.getValue() == null) {
            errorMessage.append("La date est obligatoire.\n");
        }

        if (typeComboBox.getValue() == null) {
            errorMessage.append("Le type d'événement est obligatoire.\n");
        }

        // Affichage des erreurs de validation
        if (errorMessage.length() > 0) {
            messageLabel.setText(errorMessage.toString());
            messageLabel.setStyle("-fx-text-fill: red;");
        } else {
            // Mise à jour de l'événement avec les nouvelles données
            event.setTitre(titreField.getText());
            event.setDescription(descriptionField.getText());
            event.setLieu(lieuField.getText());
            event.setDate(dateField.getValue());
            event.setNbrPlace(Integer.parseInt(nbrPlacesField.getText()));
            event.setTypeEvent(typeComboBox.getValue());

            // Appel au service pour enregistrer les modifications
            serviceEvent.modifier(event);

            // Affichage du message de succès
            messageLabel.setText("Événement modifié avec succès !");
            messageLabel.setStyle("-fx-text-fill: green;");
        }
    }

    // Méthode pour annuler la modification
    @FXML
    public void handleAnnuler(ActionEvent actionEvent) {
        // Fermer la fenêtre actuelle
        ((Stage) titreField.getScene().getWindow()).close();
    }
}

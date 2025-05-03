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

public class AddEventController {

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField heureField; // Champ pour l'heure

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

        // Vérification des champs
        if (titreField.getText().isEmpty()) errorMessage.append("Le titre est obligatoire.\n");
        if (descriptionField.getText().isEmpty()) errorMessage.append("La description est obligatoire.\n");
        if (lieuField.getText().isEmpty()) errorMessage.append("Le lieu est obligatoire.\n");

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

        TypeEvent selectedTypeEvent = typeComboBox.getValue();
        if (selectedTypeEvent == null) {
            errorMessage.append("Le type d'événement est obligatoire.\n");
        }

        if (dateField.getValue() == null) {
            errorMessage.append("La date est obligatoire.\n");
        } else if (heureField.getText().isEmpty()) {
            errorMessage.append("L'heure est obligatoire.\n");
        } else {
            try {
                // Parsing de l'heure
                LocalTime time = LocalTime.parse(heureField.getText());
            } catch (Exception e) {
                errorMessage.append("L'heure doit être au format HH:mm.\n");
            }
        }

        // Si des erreurs de validation
        if (errorMessage.length() > 0) {
            messageLabel.setText(errorMessage.toString());
            messageLabel.setStyle("-fx-text-fill: red;");
        } else {
            // Création de l'événement
            LocalDate date = dateField.getValue();
            LocalTime time = LocalTime.parse(heureField.getText());
            LocalDateTime dateTime = LocalDateTime.of(date, time);

            Event eventToAdd = new Event();
            eventToAdd.setTitre(titreField.getText());
            eventToAdd.setDescription(descriptionField.getText());
            eventToAdd.setLieu(lieuField.getText());
            eventToAdd.setNbrPlace(Integer.parseInt(nbrPlacesField.getText()));
            eventToAdd.setDate(dateTime);
            eventToAdd.setTypeEvent(selectedTypeEvent);

            // Appel au service pour ajouter l'événement
            serviceEvent.ajouter(eventToAdd);

            messageLabel.setText("Événement ajouté avec succès !");
            messageLabel.setStyle("-fx-text-fill: green;");

            clearFields();
        }
    }

    @FXML
    public void retour(ActionEvent event) {
        try {
            // Charger le fichier FXML de la liste des événements
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event/EventList.fxml"));
            Parent root = loader.load();


            // Récupérer la scène actuelle
            Scene currentScene = ((Node) event.getSource()).getScene();

            // Remplacer le contenu de la scène actuelle par la scène de la liste des événements
            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(new Scene(root  , 1000 , 700));

            // Optionnel : Afficher à nouveau la scène

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Gestion d'erreur si le fichier FXML de la liste des événements n'est pas trouvé
            messageLabel.setText("Erreur de chargement de la liste des événements.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void clearFields() {
        titreField.clear();
        descriptionField.clear();
        lieuField.clear();
        nbrPlacesField.clear();
        typeComboBox.setValue(null);
        dateField.setValue(null);
        heureField.clear();
    }
}

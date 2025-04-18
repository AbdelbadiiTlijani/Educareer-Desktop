package tn.esprit.educareer.controllers.seance;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Seance;
import tn.esprit.educareer.services.ServiceSeance;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class updateSeanceController {

    @FXML
    private TextField titreField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField dateHeureField;

    @FXML
    private TextField dureeField;

    @FXML
    private Button saveButton;

    private Seance seanceToUpdate;

    private final ServiceSeance serviceSeance = new ServiceSeance();

    // Réception des données de la séance à modifier
    public void initData(Seance seance) {
        this.seanceToUpdate = seance;
        titreField.setText(seance.getTitre());
        descriptionArea.setText(seance.getDescription());
        dateHeureField.setText(seance.getDate_heure().toString()); // afficher la date/heure actuelle
        dureeField.setText(String.valueOf(seance.getDuree()));
    }

    @FXML
    void handleUpdateButton(ActionEvent event) {
        try {
            // Récupérer les nouvelles valeurs
            String newTitre = titreField.getText();
            String newDescription = descriptionArea.getText();

            // Validation de la date/heure
            LocalDateTime newDateHeure = null;
            try {
                newDateHeure = LocalDateTime.parse(dateHeureField.getText());
            } catch (DateTimeParseException e) {
                showErrorAlert("Format incorrect de la date/heure",
                        "Le format de la date/heure doit être : yyyy-MM-ddTHH:mm");
                return; // Retourner pour empêcher la mise à jour
            }

            // Validation des autres champs
            if (newTitre.isEmpty() || newDescription.isEmpty()) {
                showErrorAlert("Champs vides", "Veuillez remplir tous les champs.");
                return;
            }

            // Validation de la durée
            int newDuree = 0;
            try {
                newDuree = Integer.parseInt(dureeField.getText());
            } catch (NumberFormatException e) {
                showErrorAlert("Durée incorrecte", "La durée doit être un nombre entier.");
                return;
            }

            // Mettre à jour l'objet Seance
            seanceToUpdate.setTitre(newTitre);
            seanceToUpdate.setDescription(newDescription);
            seanceToUpdate.setDate_heure(newDateHeure);
            seanceToUpdate.setDuree(newDuree);

            // Sauvegarder via le service
            serviceSeance.modifier(seanceToUpdate);

            // Afficher une alerte de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Mise à jour");
            alert.setHeaderText(null);
            alert.setContentText("La séance a été mise à jour avec succès !");
            alert.showAndWait();

            // Rediriger vers la page de la liste des séances
            navigateToSeanceListPage(event);

        } catch (Exception e) {
            showErrorAlert("Erreur lors de la mise à jour", "Une erreur inattendue est survenue. Veuillez réessayer.");
        }
    }

    private void showErrorAlert(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void navigateToSeanceListPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/seance/listSeance.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Une erreur est survenue lors de la navigation vers la liste des séances.");
        }
    }

    @FXML
    void handleBackButton(ActionEvent event) {
        navigateToSeanceListPage(event);
    }
}

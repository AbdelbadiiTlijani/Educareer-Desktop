package tn.esprit.educareer.controllers.TypeEvent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.educareer.models.TypeEvent;
import tn.esprit.educareer.services.ServiceTypeEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddTypeEventController implements Initializable {

    @FXML
    private TextField nomEField;

    @FXML
    private Button typeeventadd;

    private final ServiceTypeEvent serviceTypeEvent = new ServiceTypeEvent();

    @FXML
    void typeeventadd(ActionEvent event) {
        String nomE = nomEField.getText().trim();

        if (nomE.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom du type ne peut pas être vide.");
            return;
        }

        TypeEvent newTypeEvent = new TypeEvent();
        newTypeEvent.setNomE(nomE);

        serviceTypeEvent.ajouter(newTypeEvent);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Type d'événement ajouté avec succès.");
        goBackToTypeEventList();
    }

    private void goBackToTypeEventList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TypeEvent/TypeEvent.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) typeeventadd.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de revenir à la liste des types.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialisation si nécessaire
    }
}

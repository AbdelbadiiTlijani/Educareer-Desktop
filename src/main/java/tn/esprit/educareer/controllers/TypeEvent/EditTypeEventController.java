package tn.esprit.educareer.controllers.TypeEvent;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.educareer.models.TypeEvent;
import tn.esprit.educareer.services.ServiceTypeEvent;

public class EditTypeEventController {

    @FXML
    private TextField nomField;  // Champ pour le nom du type d'événement

    @FXML
    private Label messageLabel;  // Label pour afficher les messages d'erreur ou de succès

    private final ServiceTypeEvent service = new ServiceTypeEvent();  // Service pour la gestion du type d'événement
    private TypeEvent selectedTypeEvent;  // Type d'événement sélectionné à modifier

    // Méthode pour initialiser les données du type d'événement sélectionné
    public void setTypeEvent(TypeEvent typeEvent) {
        this.selectedTypeEvent = typeEvent;
        nomField.setText(typeEvent.getNomE());  // Pré-remplir le champ avec le nom actuel
    }

    // Méthode appelée lorsque l'utilisateur clique sur le bouton "Modifier"
    @FXML
    private void handleModifier() {
        String nom = nomField.getText().trim();  // Récupérer le texte du champ nom

        if (nom.isEmpty()) {
            messageLabel.setText("Le nom ne peut pas être vide.");  // Vérification si le nom est vide
            return;
        }

        selectedTypeEvent.setNomE(nom);  // Mettre à jour l'objet avec le nouveau nom
        service.modifier(selectedTypeEvent);  // Appeler la méthode pour enregistrer les changements dans la base de données
        messageLabel.setStyle("-fx-text-fill: green;");  // Changer la couleur du message en vert pour indiquer le succès
        messageLabel.setText("Type d'événement modifié avec succès.");  // Afficher le message de succès
    }

    // Méthode appelée lorsque l'utilisateur clique sur le bouton "Annuler"
    @FXML
    private void handleAnnuler() {
        Stage stage = (Stage) nomField.getScene().getWindow();  // Obtenir la fenêtre actuelle
        stage.close();  // Fermer la fenêtre
    }
}

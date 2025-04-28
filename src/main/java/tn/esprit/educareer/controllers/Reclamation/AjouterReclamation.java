package tn.esprit.educareer.controllers.Reclamation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import tn.esprit.educareer.models.Reclamation;
import tn.esprit.educareer.models.TypeReclamation;
import tn.esprit.educareer.services.ReclamationService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;


import java.io.IOException;
import java.time.LocalDateTime;


import java.util.List;

public class AjouterReclamation {

    @FXML
    private TextField sujet;
    @FXML
    private TextField description;
    @FXML
    private ComboBox<TypeReclamation> typeReclamation;

    private List<TypeReclamation> allTypes;
    private ReclamationService reclamationService;

    @FXML
    public void initialize() {
        reclamationService = new ReclamationService();
        allTypes = reclamationService.getAllTypes();
        typeReclamation.getItems().addAll(allTypes);
    }

    @FXML
    private void handleTyping(KeyEvent event) {
        suggestType();
    }

    private void suggestType() {
        String texte = (sujet.getText() + " " + description.getText()).toLowerCase();

        for (TypeReclamation type : allTypes) {
            String nom = type.getNom().toLowerCase();
            if (texte.contains(nom)) {
                typeReclamation.setValue(type);
                return;
            }
        }

        typeReclamation.setValue(null); // Si aucun type détecté
    }

    @FXML
    private void save(ActionEvent event) {
        if (sujet.getText().isEmpty() || description.getText().isEmpty() || typeReclamation.getValue() == null) {
            // Affichage message d'erreur visuelle
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs !");
            alert.showAndWait();
            return;
        }

        Reclamation newRec = new Reclamation();
        newRec.setSujet(sujet.getText());
        newRec.setDescription(description.getText());
        newRec.setTypeReclamation(typeReclamation.getValue());
        newRec.setCreatedAt(LocalDateTime.now());

        reclamationService.ajouter(newRec);

        // Affichage message de succès
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Succès");
        successAlert.setHeaderText(null);
        successAlert.setContentText("Réclamation enregistrée avec succès !");
        successAlert.showAndWait();

        // Redirection vers Student.fxml
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/Student.fxml"));
            Parent root = loader.load();
            sujet.getScene().setRoot(root);  // remplacer la scène actuelle
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void handleBackButton() {
        System.out.println("Retour au menu précédent");
        // À toi d'ajouter la navigation vers ton menu
    }
}

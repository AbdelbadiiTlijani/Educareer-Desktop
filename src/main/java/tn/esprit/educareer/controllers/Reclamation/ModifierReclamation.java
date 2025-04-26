package tn.esprit.educareer.controllers.Reclamation;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Reclamation;
import tn.esprit.educareer.models.TypeReclamation;
import tn.esprit.educareer.services.ReclamationService;
import tn.esprit.educareer.services.TypeReclamationService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ModifierReclamation implements Initializable {
    // Liens avec le fichier FXML
    @FXML
    private TextField Description;

    @FXML
    private TextField Sujet;

    @FXML
    private ComboBox<TypeReclamation> Type; // Assurez-vous que l'id correspond à celui dans FXML

    private final ReclamationService reclamationService = new ReclamationService();
    private final TypeReclamationService typeReclamationService = new TypeReclamationService();

    private Reclamation selectedReclamation; // Réclamation à modifier

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Chargement des types de réclamations dans le ComboBox
            List<TypeReclamation> types = typeReclamationService.getAll();
            Type.setItems(FXCollections.observableArrayList(types));

            // Personnalisation de l'affichage dans le ComboBox
            Type.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(TypeReclamation item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNom());
                }
            });

            Type.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(TypeReclamation item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNom());
                }
            });

            // Si une réclamation a été passée, pré-remplir les champs
            if (selectedReclamation != null) {
                Sujet.setText(selectedReclamation.getSujet());
                Description.setText(selectedReclamation.getDescription());
                Type.setValue(selectedReclamation.getTypeReclamation());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode appelée pour initialiser la réclamation à modifier
    public void setReclamationToEdit(Reclamation reclamation) {
        this.selectedReclamation = reclamation;
        if (reclamation != null) {
            Sujet.setText(reclamation.getSujet());
            Description.setText(reclamation.getDescription());
            Type.setValue(reclamation.getTypeReclamation());
        }
    }

    // Sauvegarder les modifications de la réclamation
    @FXML
    private void save(ActionEvent event) throws IOException {
        if (selectedReclamation == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune réclamation sélectionnée.");
            return;
        }

        // Récupérer les valeurs des champs
        String sujetText = Sujet.getText().trim();
        String descriptionText = Description.getText().trim();
        TypeReclamation selectedType = Type.getValue();

        if (sujetText.isEmpty() || descriptionText.isEmpty() || selectedType == null) {
            showAlert(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir tous les champs et sélectionner un type.");
            return;
        }

        // Mettre à jour la réclamation avec les nouvelles données
        selectedReclamation.setSujet(sujetText);
        selectedReclamation.setDescription(descriptionText);
        selectedReclamation.setTypeReclamation(selectedType);

        // Sauvegarder la réclamation
        reclamationService.modifier(selectedReclamation);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation modifiée avec succès.");

        // Naviguer vers la page de liste des réclamations
        navigateToPage(event, "/reclamation/ReclamationList.fxml");
    }

    // Gérer le bouton de retour
    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            navigateToPage(event, "/reclamation/ReclamationList.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour naviguer vers une autre page
    private void navigateToPage(ActionEvent event, String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    // Afficher des alertes
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

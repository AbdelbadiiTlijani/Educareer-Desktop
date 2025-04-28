package tn.esprit.educareer.controllers.Reclamation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.educareer.models.TypeReclamation;
import tn.esprit.educareer.services.ReclamationService;
import tn.esprit.educareer.services.TypeReclamationService;

import java.io.IOException;
import java.util.List;

public class TypeReclamationController {

    @FXML
    private ListView<TypeReclamation> TypeReclamationView;

    @FXML
    private TextField searchTypeField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField descriptionField;

    private TypeReclamationService service = new TypeReclamationService();

    // Initialisation de la vue
    @FXML
    void initialize() {
        afficherTypes();
    }

    // Afficher tous les types de réclamation dans la ListView
    private void afficherTypes() {
        List<TypeReclamation> types = service.getAll();
        TypeReclamationView.getItems().setAll(types);
    }

    // Méthode pour ajouter un type de réclamation
    @FXML
    void handleAjouterType(ActionEvent event) {
        String nom = nomField.getText();
        String description = descriptionField.getText();

        // Vérification si les champs sont remplis
        if (nom.isEmpty() || description.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !");
            alert.showAndWait();
            return;
        }

        // Créer un nouvel objet TypeReclamation
        TypeReclamation nouveauType = new TypeReclamation(nom, description);
        service.ajouter(nouveauType);  // Ajouter via le service
        afficherTypes();  // Mettre à jour la liste affichée

        // Vider les champs de texte après l'ajout
        nomField.clear();
        descriptionField.clear();
    }

    // Méthode pour supprimer un type de réclamation
    @FXML
    void handleSupprimerType(ActionEvent event) {
        TypeReclamation selected = TypeReclamationView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            service.supprimer(selected.getId());  // Supprimer via le service
            afficherTypes();  // Mettre à jour la liste affichée après suppression
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un type à supprimer !");
            alert.showAndWait();
        }
    }

    // Méthode pour revenir à l'écran précédent
    @FXML
    void handleBackButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/User/AdminDashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Méthode pour rechercher un type de réclamation par son nom

    @FXML
    void handleSearch(ActionEvent event) {
        String searchQuery = searchTypeField.getText().toLowerCase();  // Récupérer la recherche en minuscule

        // Si le champ est vide, on affiche tous les types de réclamation
        if (searchQuery.isEmpty()) {
            List<TypeReclamation> allTypes = service.getAll();  // Récupérer tous les types de réclamation
            updateTypeReclamationView(allTypes);  // Mettre à jour la vue avec tous les types
            return;
        }

        // Si le champ n'est pas vide, on effectue la recherche par nom
        List<TypeReclamation> filteredTypes = service.searchByName(searchQuery);  // Appeler la méthode searchByName

        // Mettre à jour la vue avec les types de réclamation filtrés
        updateTypeReclamationView(filteredTypes);  // Mettre à jour la vue avec les types filtrés
    }

    // Mise à jour de la vue avec la liste des types de réclamation
    private void updateTypeReclamationView(List<TypeReclamation> types) {
        TypeReclamationView.getItems().setAll(types);  // Mettre à jour les éléments de la ListView
    }

}

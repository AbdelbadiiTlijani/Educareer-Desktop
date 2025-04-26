package tn.esprit.educareer.controllers.Reclamation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import tn.esprit.educareer.models.Reclamation;
import tn.esprit.educareer.models.TypeReclamation;
import tn.esprit.educareer.services.ReclamationService;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;



import java.io.IOException;

public class ModifierReclamation {

    @FXML
    private TextField Description;

    @FXML
    private TextField Sujet;

    @FXML
    private ComboBox<TypeReclamation> Type;

    @FXML
    private ListView<Reclamation> offreListView;

    private Reclamation selectedReclamation;
    private ReclamationService reclamationService;

    public ModifierReclamation() {
        reclamationService = new ReclamationService();  // Assurez-vous que la classe ReclamationService est correctement instanciée
    }

    // Cette méthode est appelée pour remplir les champs de la réclamation à modifier
    public void setReclamationToEdit(Reclamation item) {
        selectedReclamation = item;

        // Remplir les champs avec les informations de la réclamation existante
        Sujet.setText(item.getSujet());
        Description.setText(item.getDescription());

        // Sélectionner le type de réclamation dans la ComboBox
        if (item.getTypeReclamation() != null) {
            Type.getSelectionModel().select(item.getTypeReclamation());
        }
    }

    // Action de retour (peut-être pour revenir à la liste des réclamations)
    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        // Charger la scène de la liste des réclamations
        Parent root = FXMLLoader.load(getClass().getResource("/Reclamation/ReclamationList.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);  // Change de scène pour afficher la liste des réclamations
    }

    // Enregistrer les modifications de la réclamation
    @FXML
    void save(ActionEvent event) throws IOException {
        // Récupérer les valeurs du formulaire
        String sujetText = Sujet.getText();
        String descriptionText = Description.getText();
        TypeReclamation selectedType = Type.getValue(); // Récupérer le type de réclamation sélectionné

        // Vérification des champs
        if (sujetText.isEmpty() || descriptionText.isEmpty() || selectedType == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs requis");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs, y compris le type de réclamation.");
            alert.showAndWait();
            return;
        }

        // Mettre à jour l'objet réclamation avec les nouvelles valeurs
        if (selectedReclamation != null) {
            selectedReclamation.setSujet(sujetText);
            selectedReclamation.setDescription(descriptionText);
            selectedReclamation.setTypeReclamation(selectedType); // Associer le type de réclamation

            // Sauvegarder les modifications via le service
            reclamationService.modifier(selectedReclamation);

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Modification réussie");
            alert.setHeaderText(null);
            alert.setContentText("Réclamation modifiée avec succès.");
            alert.showAndWait();
        }

        // Retourner à la liste des réclamations après modification
        handleBackButton(event);  // Vous pouvez utiliser la méthode handleBackButton pour revenir à la liste des réclamations
    }

}

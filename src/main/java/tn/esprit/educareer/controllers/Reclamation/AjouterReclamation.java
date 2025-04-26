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
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class AjouterReclamation implements Initializable {

    @FXML
    private TextField description;

    @FXML
    private Button handlback;

    @FXML
    private ListView<Reclamation> reclamationListView;

    @FXML
    private TextField sujet;

    @FXML
    private ComboBox<TypeReclamation> typeReclamation;

    private final ReclamationService reclamationService = new ReclamationService();
    private final TypeReclamationService typeReclamationService = new TypeReclamationService();

    @FXML
    void handleBackButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Reclamation/ReclamationList.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void save(ActionEvent event) throws IOException {
        // Récupérer les valeurs du formulaire
        String sujetText = sujet.getText();
        String descriptionText = description.getText();
        TypeReclamation selectedType = typeReclamation.getValue();

        // Vérification des champs
        if (sujetText.isEmpty() || descriptionText.isEmpty() || selectedType == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs requis");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs et sélectionner un type.");
            alert.showAndWait();
            return;
        }

        // Création de l'objet Reclamation
        Reclamation reclamation = new Reclamation();
        reclamation.setSujet(sujetText);
        reclamation.setDescription(descriptionText);
        reclamation.setTypeReclamation(selectedType);
        reclamation.setCreatedAt(LocalDateTime.now());

        // Ajout via service
        reclamationService.ajouter(reclamation);

        // Confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Réclamation enregistrée avec succès.");
        alert.showAndWait();

        // Redirection vers la liste
        Parent root = FXMLLoader.load(getClass().getResource("/reclamation/ReclamationList.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Charger tous les types de réclamation
            List<TypeReclamation> types = typeReclamationService.getAll();
            typeReclamation.setItems(FXCollections.observableArrayList(types));

            // Afficher seulement le nom du type
            typeReclamation.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(TypeReclamation item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNom());
                }
            });

            typeReclamation.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(TypeReclamation item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNom());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

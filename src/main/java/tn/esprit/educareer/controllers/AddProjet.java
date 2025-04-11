package tn.esprit.educareer.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import tn.esprit.educareer.models.CategorieProjet;
import tn.esprit.educareer.models.Projet;
import tn.esprit.educareer.services.ServiceCategorieProjet;
import tn.esprit.educareer.services.ServiceProjet;

import java.util.List;

public class AddProjet {

    @FXML
    private TextField titreField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextArea contenuField;
    @FXML
    private ComboBox<CategorieProjet> categorieComboBox;  // Remplacer Integer par CategorieProjet
    @FXML
    private TextField formateurField;

    private final ServiceProjet serviceProjet = new ServiceProjet();

    @FXML
    public void initialize() {
        // Charger les catégories dans le ComboBox
        ServiceCategorieProjet serviceCategorie = new ServiceCategorieProjet();
        List<CategorieProjet> categories = serviceCategorie.getAll(); // Méthode qui retourne List<CategorieProjet>

        // Ajouter les catégories au ComboBox
        categorieComboBox.getItems().addAll(categories);

        // Pour bien afficher les noms dans le ComboBox
        categorieComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(CategorieProjet object) {
                return object != null ? object.getCategorie() : "";
            }

            @Override
            public CategorieProjet fromString(String string) {
                return null; // non utilisé
            }
        });
    }

    @FXML
    private void ajouterProjet() {
        // Vérifier que tout est rempli
        if (categorieComboBox.getValue() == null || titreField.getText().isEmpty() ||
                descriptionField.getText().isEmpty() || contenuField.getText().isEmpty() ||
                formateurField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        try {
            CategorieProjet selectedCategorie = categorieComboBox.getValue();
            int categorieId = selectedCategorie.getId();
            String titre = titreField.getText();
            String description = descriptionField.getText();
            String contenu = contenuField.getText();
            int formateurId = Integer.parseInt(formateurField.getText());

            Projet projet = new Projet(categorieId, titre, description, contenu, formateurId);
            serviceProjet.ajouter(projet);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Projet ajouté avec succès !");
            clearFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID du formateur doit être un nombre.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        titreField.clear();
        descriptionField.clear();
        contenuField.clear();
        formateurField.clear();
        categorieComboBox.setValue(null);
    }
}

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
    private ComboBox<CategorieProjet> categorieComboBox;
    @FXML
    private TextField formateurField;

    private final ServiceProjet serviceProjet = new ServiceProjet();

    @FXML
    public void initialize() {
        ServiceCategorieProjet serviceCategorie = new ServiceCategorieProjet();
        List<CategorieProjet> categories = serviceCategorie.getAll();
        categorieComboBox.getItems().addAll(categories);

        categorieComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(CategorieProjet object) {
                return object != null ? object.getCategorie() : "";
            }

            @Override
            public CategorieProjet fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void ajouterProjet() {
        if (categorieComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Champ manquant", "Veuillez sélectionner une catégorie.");
            return;
        }

        if (titreField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ manquant", "Le titre du projet est requis.");
            return;
        }

        if (descriptionField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ manquant", "La description du projet est requise.");
            return;
        }

        if (contenuField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ manquant", "Le contenu du projet est requis.");
            return;
        }

        if (formateurField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ manquant", "L'ID du formateur est requis.");
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

package tn.esprit.educareer.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.educareer.models.CategorieProjet;
import tn.esprit.educareer.services.ServiceCategorieProjet;

import java.util.List;

public class UpdateCategorieProjet {

    @FXML
    private ComboBox<String> categorieComboBox;

    @FXML
    private TextField nouvelleCategorieField;

    private CategorieProjet categorie;

    private final ServiceCategorieProjet scp = new ServiceCategorieProjet();
    private ObservableList<String> listeCategories = FXCollections.observableArrayList();

    public void initialize() {
        List<CategorieProjet> categoriesFromDB = scp.getAll();
        for (CategorieProjet cat : categoriesFromDB) {
            listeCategories.add(cat.getCategorie());
        }
        listeCategories.add("Autre");
        categorieComboBox.setItems(listeCategories);

        // Gérer le rendu conditionnel
        categorieComboBox.setOnAction(event -> {
            String selected = categorieComboBox.getValue();
            if ("Autre".equalsIgnoreCase(selected)) {
                nouvelleCategorieField.setVisible(true);
            } else {
                nouvelleCategorieField.setVisible(false);
            }
        });
    }

    public void setCategorie(CategorieProjet cat) {
        this.categorie = cat;
        categorieComboBox.setValue(cat.getCategorie());
    }

    @FXML
    private void modifierCategorie() {
        String selectedCategorie = categorieComboBox.getValue();

        if (selectedCategorie == null || selectedCategorie.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez sélectionner une catégorie.");
            return;
        }

        String finalCategorie;

        if ("Autre".equalsIgnoreCase(selectedCategorie)) {
            String nouvelleCat = nouvelleCategorieField.getText().trim();
            if (nouvelleCat.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez entrer le nom de la nouvelle catégorie.");
                return;
            }

            // Vérifier si elle existe (insensible à la casse)
            CategorieProjet existante = scp.findByNameIgnoreCase(nouvelleCat);
            if (existante != null) {
                finalCategorie = existante.getCategorie();
            } else {
                // Ajouter dans la BD
                CategorieProjet nouvelle = new CategorieProjet(nouvelleCat);
                scp.ajouter(nouvelle);
                finalCategorie = nouvelleCat;
            }
        } else {
            finalCategorie = selectedCategorie;
        }

        categorie.setCategorie(finalCategorie);
        scp.modifier(categorie);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie modifiée avec succès !");
        Stage stage = (Stage) categorieComboBox.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

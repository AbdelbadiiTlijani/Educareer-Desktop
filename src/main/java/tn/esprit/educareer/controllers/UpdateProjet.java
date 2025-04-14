package tn.esprit.educareer.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import tn.esprit.educareer.models.CategorieProjet;
import tn.esprit.educareer.models.Projet;
import tn.esprit.educareer.services.ServiceCategorieProjet;
import tn.esprit.educareer.services.ServiceProjet;

public class UpdateProjet {

    @FXML
    private TextField titreField;
    @FXML
    private TextArea contenuField;
    @FXML
    private ComboBox<CategorieProjet> categorieComboBox;

    private final ServiceProjet serviceProjet = new ServiceProjet();
    private final ServiceCategorieProjet serviceCategorie = new ServiceCategorieProjet();

    private Projet projet;

    @FXML
    public void initialize() {
        // Remplir le ComboBox des catégories
        ObservableList<CategorieProjet> categories = FXCollections.observableArrayList(serviceCategorie.getAll());
        categorieComboBox.setItems(categories);

        categorieComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(CategorieProjet categorie) {
                return (categorie != null) ? categorie.getCategorie() : "";
            }

            @Override
            public CategorieProjet fromString(String string) {
                return categorieComboBox.getItems().stream()
                        .filter(cat -> cat.getCategorie().equals(string))
                        .findFirst().orElse(null);
            }
        });
    }

    public void setProjet(Projet projet) {
        this.projet = projet;

        if (projet != null) {
            titreField.setText(projet.getTitre());
            contenuField.setText(projet.getContenu());

            // Sélectionner la catégorie existante
            for (CategorieProjet cat : categorieComboBox.getItems()) {
                if (cat.getId() == projet.getCategorie_id()) {
                    categorieComboBox.setValue(cat);
                    break;
                }
            }
        }
    }

    @FXML
    private void updateProjet() {
        CategorieProjet selectedCategorie = categorieComboBox.getValue();
        String titre = titreField.getText();
        String contenu = contenuField.getText();

        if (selectedCategorie == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une catégorie.");
            alert.show();
            return;
        }

        projet.setCategorie_id(selectedCategorie.getId());
        projet.setTitre(titre);
        projet.setContenu(contenu);

        serviceProjet.modifier(projet);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Projet mis à jour avec succès !");
        alert.show();
    }
}

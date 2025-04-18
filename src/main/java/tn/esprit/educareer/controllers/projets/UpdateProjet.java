package tn.esprit.educareer.controllers.projets;

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
    private TextArea descriptionField;


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
        String titre = titreField.getText().trim();
        String contenu = contenuField.getText().trim();
        String description = descriptionField.getText().trim();

        if (description.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La description du projet ne peut pas être vide.");
            return;
        }
        CategorieProjet selectedCategorie = categorieComboBox.getValue();

        if (selectedCategorie == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une catégorie.");
            return;
        }

        if (titre.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le titre du projet ne peut pas être vide.");
            return;
        }

        if (contenu.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le contenu du projet ne peut pas être vide.");
            return;
        }

        projet.setCategorie_id(selectedCategorie.getId());
        projet.setTitre(titre);
        projet.setContenu(contenu);
        projet.setDescription(description);

        serviceProjet.modifier(projet);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Projet mis à jour avec succès !");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package tn.esprit.educareer.controllers.projets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tn.esprit.educareer.models.CategorieProjet;
import tn.esprit.educareer.models.Projet;
import tn.esprit.educareer.services.ServiceCategorieProjet;
import tn.esprit.educareer.services.ServiceProjet;

import java.io.IOException;
import java.net.URL;

public class UpdateProjet {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private TextArea contenuField;
    @FXML private ComboBox<CategorieProjet> categorieComboBox;
    @FXML private TextField nouvelleCategorieField;

    private final ServiceProjet serviceProjet = new ServiceProjet();
    private final ServiceCategorieProjet serviceCategorie = new ServiceCategorieProjet();
    private Projet projet;

    @FXML
    public void initialize() {
        ObservableList<CategorieProjet> categories = FXCollections.observableArrayList(serviceCategorie.getAll());

        // Ajouter l'option "Autre"
        CategorieProjet autre = new CategorieProjet(-1, "Autre");
        categories.add(autre);

        categorieComboBox.setItems(categories);

        categorieComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(CategorieProjet categorie) {
                return (categorie != null) ? categorie.getCategorie() : "";
            }

            @Override
            public CategorieProjet fromString(String string) {
                return null;
            }
        });
    }

    public void setProjet(Projet projet) {
        this.projet = projet;

        if (projet != null) {
            titreField.setText(projet.getTitre());
            descriptionField.setText(projet.getDescription());
            contenuField.setText(projet.getContenu());

            for (CategorieProjet cat : categorieComboBox.getItems()) {
                if (cat.getId() == projet.getCategorie_id()) {
                    categorieComboBox.setValue(cat);
                    break;
                }
            }

            handleCategorieSelection(); // pour afficher le champ si "Autre" est déjà sélectionné
        }
    }

    @FXML
    private void handleCategorieSelection() {
        CategorieProjet selected = categorieComboBox.getValue();
        boolean isAutre = selected != null && "Autre".equalsIgnoreCase(selected.getCategorie());
        nouvelleCategorieField.setVisible(isAutre);
        nouvelleCategorieField.setManaged(isAutre);
    }

    @FXML
    private void updateProjet() {
        String titre = titreField.getText().trim();
        String description = descriptionField.getText().trim();
        String contenu = contenuField.getText().trim();

        if (titre.isEmpty() || description.isEmpty() || contenu.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        CategorieProjet selectedCategorie = categorieComboBox.getValue();
        CategorieProjet finalCategorie;

        if (selectedCategorie != null && "Autre".equalsIgnoreCase(selectedCategorie.getCategorie())) {
            String newCatName = nouvelleCategorieField.getText().trim();
            if (newCatName.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer le nom de la nouvelle catégorie.");
                return;
            }

            CategorieProjet existing = serviceCategorie.findByNameIgnoreCase(newCatName);
            if (existing != null) {
                finalCategorie = existing;
            } else {
                CategorieProjet newCategorie = new CategorieProjet();
                newCategorie.setCategorie(newCatName);
                serviceCategorie.ajouter(newCategorie);
                finalCategorie = serviceCategorie.findByNameIgnoreCase(newCatName);
            }
        } else {
            finalCategorie = selectedCategorie;
        }

        if (finalCategorie == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Catégorie invalide.");
            return;
        }

        projet.setTitre(titre);
        projet.setDescription(description);
        projet.setContenu(contenu);
        projet.setCategorie_id(finalCategorie.getId());

        serviceProjet.modifier(projet);
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Projet modifié avec succès !");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        navigateToPage(event, "/Projet/ReadProjets.fxml");
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) throw new IOException("FXML file not found at: " + path);
        Parent root = FXMLLoader.load(fxmlLocation);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}

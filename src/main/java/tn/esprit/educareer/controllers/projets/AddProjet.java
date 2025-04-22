package tn.esprit.educareer.controllers.projets;

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
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ServiceCategorieProjet;
import tn.esprit.educareer.services.ServiceProjet;
import tn.esprit.educareer.utils.UserSession;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class AddProjet {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private TextArea contenuField;
    @FXML private ComboBox<CategorieProjet> categorieComboBox;
    @FXML private TextField formateurField;
    @FXML private TextField nouvelleCategorieField;

    private final ServiceProjet serviceProjet = new ServiceProjet();
    private final ServiceCategorieProjet serviceCategorie = new ServiceCategorieProjet();

    @FXML
    public void initialize() {
        List<CategorieProjet> categories = serviceCategorie.getAll();
        categorieComboBox.getItems().addAll(categories);

        // Ajouter "Autre"
        CategorieProjet autre = new CategorieProjet(-1, "Autre");
        categorieComboBox.getItems().add(autre);

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

        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            formateurField.setText(String.valueOf(currentUser.getId()));
            formateurField.setDisable(true);
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
    private void ajouterProjet() {
        try {
            if (titreField.getText().trim().isEmpty() ||
                    descriptionField.getText().trim().isEmpty() ||
                    contenuField.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Champ manquant", "Tous les champs doivent être remplis.");
                return;
            }

            int formateurId = Integer.parseInt(formateurField.getText());
            String titre = titreField.getText();
            String description = descriptionField.getText();
            String contenu = contenuField.getText();

            CategorieProjet selectedCategorie = categorieComboBox.getValue();
            CategorieProjet finalCategorie;

            if (selectedCategorie != null && "Autre".equalsIgnoreCase(selectedCategorie.getCategorie())) {
                String newCatName = nouvelleCategorieField.getText().trim();
                if (newCatName.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Champ manquant", "Veuillez entrer le nom de la nouvelle catégorie.");
                    return;
                }

                // Vérifier si la catégorie existe déjà (insensible à la casse)
                CategorieProjet existing = serviceCategorie.findByNameIgnoreCase(newCatName);
                if (existing != null) {
                    finalCategorie = existing;
                } else {
                    // Ajouter nouvelle catégorie
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

            Projet projet = new Projet(finalCategorie.getId(), titre, description, contenu, formateurId);
            serviceProjet.ajouter(projet);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Projet ajouté avec succès !");
            clearFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID Formateur invalide.");
        }
    }

    private void clearFields() {
        titreField.clear();
        descriptionField.clear();
        contenuField.clear();
        nouvelleCategorieField.clear();
        categorieComboBox.setValue(null);
        nouvelleCategorieField.setVisible(false);
        nouvelleCategorieField.setManaged(false);
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

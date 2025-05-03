package tn.esprit.educareer.controllers.projets;

import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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
    @FXML private ComboBox<CategorieProjet> categorieComboBox;
    @FXML private TextField formateurField;
    @FXML private TextField nouvelleCategorieField;
    @FXML private WebView contenuEditor;

    private WebEngine webEngine;

    private final ServiceProjet serviceProjet = new ServiceProjet();
    private final ServiceCategorieProjet serviceCategorie = new ServiceCategorieProjet();

    @FXML
    public void initialize() {
        List<CategorieProjet> categories = serviceCategorie.getAll();
        categorieComboBox.getItems().addAll(categories);

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

        // Charger TinyMCE dans WebView
        webEngine = contenuEditor.getEngine();
        webEngine.loadContent("""
            <!DOCTYPE html>
            <html>
            <head>
              <script src="https://cdn.tiny.cloud/1/wj1srxz8nfpm68y705nwnchk9k25rjh7tvxk845pdn9lgaxe/tinymce/6/tinymce.min.js" referrerpolicy="origin"></script>
              <script>
                tinymce.init({
                  selector: '#editor',
                  height: 250
                });
              </script>
            </head>
            <body>
              <textarea id="editor">Écrivez ici le contenu du projet...</textarea>
            </body>
            </html>
        """);
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
                    descriptionField.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Champ manquant", "Tous les champs doivent être remplis.");
                return;
            }

            int formateurId = Integer.parseInt(formateurField.getText());
            String titre = titreField.getText();
            String description = descriptionField.getText();

            // Attendre le chargement du contenu TinyMCE
            webEngine.executeScript("tinymce.triggerSave();");
            String contenu = (String) webEngine.executeScript("document.getElementById('editor').value");

            if (contenu.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Champ manquant", "Contenu du projet est vide.");
                return;
            }

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

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du projet.");
        }
    }

    private void clearFields() {
        titreField.clear();
        descriptionField.clear();
        nouvelleCategorieField.clear();
        categorieComboBox.setValue(null);
        nouvelleCategorieField.setVisible(false);
        nouvelleCategorieField.setManaged(false);
        webEngine.executeScript("tinymce.get('editor').setContent('');");
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

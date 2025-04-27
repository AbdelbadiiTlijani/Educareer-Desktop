package tn.esprit.educareer.controllers.projets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import tn.esprit.educareer.services.ServiceCategorieProjet;
import tn.esprit.educareer.services.ServiceProjet;

import java.io.IOException;
import java.net.URL;

public class UpdateProjet {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private WebView contenuEditor;
    @FXML private ComboBox<CategorieProjet> categorieComboBox;
    @FXML private TextField nouvelleCategorieField;

    private final ServiceProjet serviceProjet = new ServiceProjet();
    private final ServiceCategorieProjet serviceCategorie = new ServiceCategorieProjet();
    private Projet projet;
    private WebEngine webEngine;
    private boolean editorReady = false; // pour éviter l'injection prématurée

    @FXML
    public void initialize() {
        webEngine = contenuEditor.getEngine();

        ObservableList<CategorieProjet> categories = FXCollections.observableArrayList(serviceCategorie.getAll());
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

        webEngine = contenuEditor.getEngine();
        webEngine.loadContent("""
        <!DOCTYPE html>
        <html>
        <head>
          <script src="https://cdn.tiny.cloud/1/wj1srxz8nfpm68y705nwnchk9k25rjh7tvxk845pdn9lgaxe/tinymce/6/tinymce.min.js" referrerpolicy="origin"></script>
          <script>
            function initTinyMCE(content) {
              tinymce.init({
                selector: '#editor',
                height: 250,
                setup: function (editor) {
                  editor.on('init', function () {
                    editor.setContent(content); // injecte ton ancien texte ici
                    window.editor = editor;
                  });
                }
              });
            }
          </script>
        </head>
        <body>
          <textarea id="editor"></textarea>
        </body>
        </html>
    """);

        // Attendre que le WebView soit chargé
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                // une fois chargé, injecter ton ancien texte !
                String ancienContenu = projet.getContenu(); // remplace par ta variable
                ancienContenu = ancienContenu.replace("\"", "\\\"").replace("\n", "\\n"); // échapper les guillemets
                webEngine.executeScript("initTinyMCE(\"" + ancienContenu + "\");");
            }
        });
    }

    public void setProjet(Projet projet) {
        this.projet = projet;

        if (projet != null) {
            titreField.setText(projet.getTitre());
            descriptionField.setText(projet.getDescription());

            for (CategorieProjet cat : categorieComboBox.getItems()) {
                if (cat.getId() == projet.getCategorie_id()) {
                    categorieComboBox.setValue(cat);
                    break;
                }
            }

            handleCategorieSelection();
            if (editorReady) {
                setTinyMCEContent(projet.getContenu());
            }
        }
    }

    private void setTinyMCEContent(String content) {
        if (editorReady) {
            try {
                String escapedContent = escapeJS(content);
                webEngine.executeScript("window.editor.setContent('" + escapedContent + "');");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getTinyMCEContent() {
        try {
            Object result = webEngine.executeScript("window.editor.getContent();");
            return result != null ? result.toString().trim() : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String escapeJS(String input) {
        return input.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
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
        String contenu = getTinyMCEContent();

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

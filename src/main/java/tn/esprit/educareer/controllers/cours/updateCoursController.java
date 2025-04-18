package tn.esprit.educareer.controllers.cours;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.educareer.models.CategorieCours;
import tn.esprit.educareer.models.Cours;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ServiceCategorieCours;
import tn.esprit.educareer.services.ServiceCours;
import tn.esprit.educareer.services.ServiceUser;

public class updateCoursController {

    @FXML
    private TextField nomCours;

    @FXML
    private TextField documentCours;

    @FXML
    private TextField imageCours;

    @FXML
    private TextField requirementCours;

    @FXML
    private ComboBox<CategorieCours> categorieCoursComboBox;

    @FXML
    private ComboBox<User> formateurComboBox;

    private final ServiceCours serviceCours = new ServiceCours();
    private final ServiceCategorieCours serviceCategorie = new ServiceCategorieCours();
    private final ServiceUser serviceUser = new ServiceUser();

    private Cours coursToUpdate;

    public void initData(Cours cours) {
        this.coursToUpdate = cours;

        // Pré-remplir les champs
        nomCours.setText(cours.getNom());
        documentCours.setText(cours.getDocument());
        imageCours.setText(cours.getImage());
        requirementCours.setText(cours.getRequirement());

        // Remplir les ComboBox
        categorieCoursComboBox.getItems().addAll(serviceCategorie.getAll());
        formateurComboBox.getItems().addAll(serviceUser.getAll());

        // Sélectionner la catégorie et le formateur actuels
        categorieCoursComboBox.setValue(cours.getCategorie());
        formateurComboBox.setValue(cours.getUser());
    }

    @FXML
    private void handleUpdateCours() {
        if (coursToUpdate == null) return;

        // Mettre à jour les données
        coursToUpdate.setNom(nomCours.getText());
        coursToUpdate.setDocument(documentCours.getText());
        coursToUpdate.setImage(imageCours.getText());
        coursToUpdate.setRequirement(requirementCours.getText());
        coursToUpdate.setCategorie(categorieCoursComboBox.getValue());
        coursToUpdate.setUser(formateurComboBox.getValue());

        // Appeler le service pour mettre à jour le cours
        serviceCours.modifier(coursToUpdate);

        // Confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cours modifié");
        alert.setHeaderText(null);
        alert.setContentText("Le cours a été mis à jour avec succès !");
        alert.showAndWait();

        // Fermer la fenêtre
        Stage stage = (Stage) nomCours.getScene().getWindow();
        stage.close();
    }
}

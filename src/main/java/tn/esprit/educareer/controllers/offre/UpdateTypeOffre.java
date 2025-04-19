package tn.esprit.educareer.controllers.offre;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Type_Offre;
import tn.esprit.educareer.services.TypeOffreService;

import java.io.IOException;

public class UpdateTypeOffre {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField categorie;

    private final TypeOffreService typeOffreService = new TypeOffreService();
    private Type_Offre selectedType;

    @FXML
    public void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/offre/TypeOffreList.fxml");
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        root = FXMLLoader.load(getClass().getResource(path));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root , 1000 , 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void setTypeToEdit(Type_Offre type) {
        this.selectedType = type;
        categorie.setText(type.getCategorie());
    }

    @FXML
    public void savetype(ActionEvent event) throws IOException {
        Type_Offre updatedType = new Type_Offre();
        updatedType.setId(selectedType.getId());
        updatedType.setCategorie(categorie.getText());

        typeOffreService.modifier(updatedType);

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
}

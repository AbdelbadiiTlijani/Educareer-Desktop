package tn.esprit.educareer.controllers.offre;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Offre_Emploi;
import tn.esprit.educareer.models.Type_Offre;
import tn.esprit.educareer.services.OffreEmploiService;
import tn.esprit.educareer.services.TypeOffreService;

import java.io.IOException;
import java.net.URL;

public class AjouterTypeOffre {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ListView<?> offreListView;

    @FXML
    private TextField categorie;

    private TypeOffreService to= new TypeOffreService();

    @FXML
    void savetype(ActionEvent event) throws IOException {
        to.ajouter(new Type_Offre(
                categorie.getText()
        ));
        navigateToPage(event, "/offre/TypeList.fxml");
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        root = FXMLLoader.load(getClass().getResource(path));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }


    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/offre/OffreList.fxml");
    }



}

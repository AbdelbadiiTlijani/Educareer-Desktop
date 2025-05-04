package tn.esprit.educareer.controllers.offre;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Offre_Emploi;
import tn.esprit.educareer.services.OffreEmploiService;

import java.io.IOException;
import java.net.URL;
import java.util.List;



public class JobOffers {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public Button postulerButton;

    @FXML
    private ListView<Offre_Emploi> offreListView;

    private final OffreEmploiService offreService = new OffreEmploiService();

    public void initialize() {
        afficherOffres();
    }

    private void afficherOffres() {
        List<Offre_Emploi> offres = offreService.getAll();
        offreListView.getItems().setAll(offres);
        offreListView.setCellFactory(param -> new ListCell<Offre_Emploi>() {
            private final Label titreLabel = new Label();
            private final Label descriptionLabel = new Label();
            private final VBox vboxText = new VBox(titreLabel, descriptionLabel);
            private final HBox hBox = new HBox(20, vboxText);

            {
                titreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                descriptionLabel.setStyle("-fx-text-fill: #777;");
                vboxText.setSpacing(5);
                hBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10;");
                hBox.setSpacing(20);
            }

            @Override
            protected void updateItem(Offre_Emploi offre, boolean empty) {
                super.updateItem(offre, empty);
                if (empty || offre == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    titreLabel.setText(offre.getTitre() + " - " + offre.getLieu() + " (" + offre.getSalaire() + " DT)");
                    descriptionLabel.setText(offre.getDescoffre());
                    setGraphic(hBox);
                }
            }
        });
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        root = FXMLLoader.load(fxmlLocation);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root , 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    public void handlePostuler(ActionEvent event) {
        // Logique pour postuler ici
    }

    public void postulerButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/offre/AjouterPostulation.fxml");
    }

    @FXML
    void returnButtonAction(ActionEvent event) throws  IOException{
        navigateToPage(event, "/User/student.fxml");

    }

}

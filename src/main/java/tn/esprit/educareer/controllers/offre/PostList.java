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
import tn.esprit.educareer.models.Postulation;
import tn.esprit.educareer.services.PostulationService;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class PostList {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ListView<Postulation> postulationListView;

    private final PostulationService postulationService = new PostulationService();

    @FXML
    public void initialize() {
        afficherPostulations();
    }

    private void afficherPostulations() {
        List<Postulation> postulations = postulationService.getAll();
        postulationListView.getItems().setAll(postulations);

        postulationListView.setCellFactory(param -> new ListCell<Postulation>() {
            private final Label offreLabel = new Label();
            private final Label candidatLabel = new Label();
            private final Button supprimerBtn = new Button("Supprimer");
            private final VBox vboxText = new VBox(offreLabel, candidatLabel);
            private final HBox hBox = new HBox(20, vboxText, supprimerBtn);

            {
                offreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                candidatLabel.setStyle("-fx-text-fill: #666;");
                supprimerBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");

                vboxText.setSpacing(5);
                hBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10;");
                hBox.setSpacing(20);

                supprimerBtn.setOnAction(event -> {
                    Postulation selected = getItem();
                    if (selected != null) {
                        postulationService.supprimer(selected); // Suppression de la postulation
                        afficherPostulations(); // Rafraîchir la liste
                    }
                });
            }

            @Override
            protected void updateItem(Postulation postulation, boolean empty) {
                super.updateItem(postulation, empty);
                if (empty || postulation == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    offreLabel.setText("Offre: " + postulation.getOffre());

                    setGraphic(hBox);
                }
            }
        });
    }

    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/offre/offreList.fxml");
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        root = FXMLLoader.load(fxmlLocation);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}

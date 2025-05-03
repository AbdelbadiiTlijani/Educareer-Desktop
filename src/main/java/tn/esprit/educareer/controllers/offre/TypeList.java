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
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.educareer.models.Type_Offre;
import tn.esprit.educareer.services.TypeOffreService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class TypeList {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ListView<Type_Offre> typeListView;

    private final TypeOffreService typeService = new TypeOffreService();

    @FXML
    public void initialize() {
        afficherTypes();
    }

    private void afficherTypes() {
        List<Type_Offre> types = typeService.getAll();
        typeListView.getItems().setAll(types);

        typeListView.setCellFactory(param -> new ListCell<Type_Offre>() {
            private final Label typeLabel = new Label();
            private final Button updateBtn = new Button("Modifier");
            private final Button supprimerBtn = new Button("Supprimer");
            private final VBox vboxText = new VBox(typeLabel);
            private final HBox hBox = new HBox(20, vboxText, updateBtn, supprimerBtn);

            {
                typeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

                updateBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
                supprimerBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");

                vboxText.setSpacing(5);

                hBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10;");
                hBox.setSpacing(20);

                updateBtn.setOnAction(event -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/offre/UpdateTypeOffre.fxml"));
                        Parent root = loader.load();
                        UpdateTypeOffre controller = loader.getController();
                        controller.setTypeToEdit(getItem());

                        Stage updateStage = new Stage();
                        updateStage.setTitle("Modifier Type d'Offre");
                        updateStage.setScene(new Scene(root));
                        updateStage.setOnHidden(e -> afficherTypes()); // refresh après fermeture
                        updateStage.show();

                    } catch (IOException e) {
                        System.out.println("Erreur de navigation : " + e.getMessage());
                    }
                });

                supprimerBtn.setOnAction(event -> {
                    Type_Offre selected = getItem();
                    if (selected != null) {
                        typeService.supprimer(selected);
                        afficherTypes();
                    }
                });
            }

            @Override
            protected void updateItem(Type_Offre type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    typeLabel.setText("Catégorie: " + type.getCategorie());
                    setGraphic(hBox);
                }
            }
        });
    }



    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/offre/offreList.fxml");
    }

    @FXML
    void handleAjouterType(ActionEvent event) {
        try {
            navigateToPage(event, "/offre/AjouterTypeOffre.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        root = FXMLLoader.load(fxmlLocation);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root , 1000 , 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }



}

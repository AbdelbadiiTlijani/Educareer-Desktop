package tn.esprit.educareer.controllers.categorieCours;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.educareer.models.CategorieCours;
import tn.esprit.educareer.services.ServiceCategorieCours;

import java.io.IOException;
import java.net.URL;

public class listCategorieCoursController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private final ServiceCategorieCours serviceCategorie = new ServiceCategorieCours();

    @FXML
    private ListView<CategorieCours> userListView;

    @FXML
    public void initialize() {
        // Charger toutes les catégories
        userListView.getItems().addAll(serviceCategorie.getAll());

        // Définir la cellule personnalisée pour chaque catégorie
        userListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<CategorieCours> call(ListView<CategorieCours> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(CategorieCours cat, boolean empty) {
                        super.updateItem(cat, empty);
                        if (empty || cat == null) {
                            setGraphic(null);
                        } else {
                            HBox container = new HBox(10);
                            container.setStyle("-fx-padding: 10px; -fx-background-color: white; -fx-background-radius: 5px;");

                            VBox info = new VBox(5);
                            Label nomLabel = new Label(cat.getNom());
                            nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                            info.getChildren().add(nomLabel);

                            // Bouton de suppression
                            Button deleteButton = new Button("Supprimer");
                            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                            deleteButton.setOnAction(event -> {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("Confirmation de suppression");
                                alert.setHeaderText("Supprimer la catégorie");
                                alert.setContentText("Êtes-vous sûr de vouloir supprimer cette catégorie ?");

                                if (alert.showAndWait().get() == ButtonType.OK) {
                                    serviceCategorie.supprimer(cat);
                                    userListView.getItems().remove(cat);
                                }
                            });

                            // Bouton de modification
                            Button updateButton = new Button("Modifier");
                            updateButton.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white; -fx-font-weight: bold;");
                            updateButton.setOnAction(event -> {
                                try {
                                    // Chargement du FXML pour la mise à jour
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/categorieCours/updateCategorieCours.fxml"));
                                    Parent root = loader.load();

                                    // Récupération du contrôleur de mise à jour
                                    updateCategorieCoursController controller = loader.getController();
                                    controller.initData(cat);  // Envoie des données au contrôleur de mise à jour

                                    // Changement de scène
                                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                    stage.setScene(new Scene(root , 1000 , 700));
                                    stage.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

                            // Ajout des boutons dans le conteneur
                            container.getChildren().addAll(info, updateButton, deleteButton);
                            setGraphic(container);
                        }
                    }
                };
            }
        });
    }

    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/cours/listCours.fxml");
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

    @FXML
    private Button ViewCategorieCours;

    @FXML
    void handleAjouterCategorie(ActionEvent event) throws IOException {
        navigateToPage(event, "/categorieCours/ajouterCategorieCours.fxml");
    }
}

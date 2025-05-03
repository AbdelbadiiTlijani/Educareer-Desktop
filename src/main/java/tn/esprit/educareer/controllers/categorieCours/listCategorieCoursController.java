package tn.esprit.educareer.controllers.categorieCours;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.educareer.models.CategorieCours;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ServiceCategorieCours;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static java.util.stream.Collectors.toList;


public class listCategorieCoursController {
    private final ServiceCategorieCours serviceCategorieCours = new ServiceCategorieCours();

    @FXML
    private ListView<CategorieCours> categorieCoursListView;

    @FXML
    private TextField searchField;

    @FXML
    private Button viewBackButton;

    private Stage stage;
    private Scene scene;

    @FXML
    public void initialize() {
        serviceCategorieCours.supprimerCategoriesInutiliseesDepuisUnJour();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filterCategorieCoursList();
        });

        ObservableList<CategorieCours> observableList = FXCollections.observableArrayList(serviceCategorieCours.getAll());
        categorieCoursListView.setItems(observableList);

        categorieCoursListView.setCellFactory(listView -> new ListCell<>() {
            private final Label nomLabel = new Label();
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            private final HBox hbox = new HBox(10, nomLabel, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");

                editButton.setOnAction(this::handleEdit);
                deleteButton.setOnAction(this::handleDelete);
            }

            @Override
            protected void updateItem(CategorieCours categorie, boolean empty) {
                super.updateItem(categorie, empty);
                if (empty || categorie == null) {
                    setGraphic(null);
                } else {
                    nomLabel.setText(categorie.getNom());
                    nomLabel.setStyle("-fx-font-family: bold; -fx-font-size: 20px;-fx-text-fill: #2c3e50;");
                    setGraphic(hbox);
                }
            }

            private void handleEdit(ActionEvent event) {
                CategorieCours selected = getItem();
                if (selected != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/categorieCours/updateCategorieCours.fxml"));
                        Parent root = loader.load();

                        updateCategorieCoursController controller = loader.getController();
                        controller.initData(selected);

                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void handleDelete(ActionEvent event) {
                CategorieCours selected = getItem();
                if (selected != null) {
                    serviceCategorieCours.supprimer(selected);
                    getListView().getItems().remove(selected);
                }
            }
        });
    }



    private void filterCategorieCoursList() {
        String searchText = searchField.getText().toLowerCase().trim();

        List<CategorieCours> filtered = serviceCategorieCours.getAll().stream().filter(categorieCours -> categorieCours.getNom().toLowerCase().contains(searchText)).toList();
        categorieCoursListView.getItems().setAll(filtered);
    }


    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/User/FormateurDashboard.fxml");
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}

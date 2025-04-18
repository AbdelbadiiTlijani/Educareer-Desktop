package tn.esprit.educareer.controllers.projets;
import javafx.event.ActionEvent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Projet;
import tn.esprit.educareer.services.ServiceProjet;

import java.io.IOException;
import java.net.URL;

public class ReadProjets {

    @FXML
    private TableView<Projet> tableProjets;
    @FXML
    private TableColumn<Projet, String> colTitre;
    @FXML
    private TableColumn<Projet, String> colCategorie;
    @FXML
    private TableColumn<Projet, String> colFormateur;
    @FXML
    private TableColumn<Projet, Void> colActions;
    private Stage stage;
    private Scene scene;
    private Parent root;


    private ServiceProjet serviceProjet = new ServiceProjet();

    @FXML
    public void initialize() {
        ObservableList<Projet> list = FXCollections.observableArrayList(serviceProjet.getAll());
        tableProjets.setItems(list);

        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorieNom"));


        addButtonToTable();

    }


    @FXML
    private void goToAjoutProjet() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Projet/AddProjet.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) tableProjets.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addButtonToTable() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Modifier");
            private final Button btnDelete = new Button("Supprimer");

            {
                btnEdit.setStyle("-fx-background-color: #00bfff; -fx-text-fill: white;");
                btnDelete.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
                btnEdit.setOnAction(event -> {
                    Projet projet = getTableView().getItems().get(getIndex());

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Projet/UpdateProjet.fxml"));
                        Parent root = loader.load();

                        // Récupérer le contrôleur de UpdateProjet
                        UpdateProjet controller = loader.getController();

                        // Passer le projet à modifier
                        controller.setProjet(projet); // Tu dois créer cette méthode dans UpdateProjet.java

                        // Affichage de la scène (tu peux faire un nouveau Stage ou remplacer l’actuel)
                        Stage stage = new Stage();
                        stage.setTitle("Modifier Projet");
                        stage.setScene(new Scene(root));
                        stage.show();

                        // Optionnel : cacher la fenêtre actuelle
                        // ((Stage) tableProjets.getScene().getWindow()).hide();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });


                btnDelete.setOnAction(event -> {
                    Projet projet = getTableView().getItems().get(getIndex());
                    serviceProjet.supprimer(projet);
                    tableProjets.setItems(FXCollections.observableArrayList(serviceProjet.getAll()));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox pane = new HBox(10, btnEdit, btnDelete);
                    setGraphic(pane);
                }
            }
        });
    }
    @FXML
    private void handleBack(ActionEvent event) throws IOException{

        navigateToPage(event , "/User/FormateurDashboard.fxml");
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
}

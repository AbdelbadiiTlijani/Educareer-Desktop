package tn.esprit.educareer.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Projet;
import tn.esprit.educareer.services.ServiceProjet;

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


    private ServiceProjet serviceProjet = new ServiceProjet();

    @FXML
    public void initialize() {
        ObservableList<Projet> list = FXCollections.observableArrayList(serviceProjet.getAll());
        tableProjets.setItems(list);

        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie_id"));
        colFormateur.setCellValueFactory(new PropertyValueFactory<>("formateur_id"));
    }

    @FXML
    private void goToAjoutProjet() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddProjet.fxml"));
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
                    // Appeler ta logique de modification ici
                    System.out.println("Modifier: " + projet.getTitre());
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
}

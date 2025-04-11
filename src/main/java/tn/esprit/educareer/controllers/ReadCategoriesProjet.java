package tn.esprit.educareer.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.educareer.models.CategorieProjet;
import tn.esprit.educareer.services.ServiceCategorieProjet;

import java.io.IOException;

public class ReadCategoriesProjet {

    @FXML private TableView<CategorieProjet> tableCategorie;
    @FXML private TableColumn<CategorieProjet, String> colNom;
    @FXML private TableColumn<CategorieProjet, Void> colCompteur;
    @FXML private TableColumn<CategorieProjet, Void> colActions;

    ServiceCategorieProjet scp = new ServiceCategorieProjet();
    ObservableList<CategorieProjet> list;

    @FXML
    public void initialize() {
        afficherCategories();
    }

    private void afficherCategories() {
        list = FXCollections.observableArrayList(scp.getAll());

        // Colonne nom
        colNom.setCellValueFactory(new PropertyValueFactory<>("categorie"));

        // Colonne compteur automatique
        colCompteur.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (this.getTableRow() != null && !empty) {
                    int index = this.getIndex() + 1;
                    setText(String.valueOf(index));
                } else {
                    setText(null);
                }
            }
        });

        // Colonne des actions
        colActions.setCellFactory(new Callback<>() {
            @Override
            public TableCell<CategorieProjet, Void> call(TableColumn<CategorieProjet, Void> param) {
                return new TableCell<>() {
                    private final Button btnModifier = new Button("Modifier");
                    private final Button btnSupprimer = new Button("Supprimer");

                    {
                        btnModifier.setStyle("-fx-background-color: #ffc107;");
                        btnSupprimer.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

                        btnModifier.setOnAction(event -> {
                            CategorieProjet selected = getTableView().getItems().get(getIndex());
                            ouvrirUpdate(selected);
                        });

                        btnSupprimer.setOnAction(event -> {
                            CategorieProjet selected = getTableView().getItems().get(getIndex());
                            scp.supprimer(selected);
                            afficherCategories(); // Refresh après suppression
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10, btnModifier, btnSupprimer);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });

        tableCategorie.setItems(list);
    }

    private void ouvrirUpdate(CategorieProjet categorie) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateCategorieProjet.fxml"));
            Parent root = loader.load();

            UpdateCategorieProjet controller = loader.getController();
            controller.setCategorie(categorie);

            Stage stage = new Stage();
            stage.setTitle("Modifier Catégorie");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> afficherCategories()); // refresh après fermeture de la fenêtre
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAjoutCategorie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddCategorieProjet.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) tableCategorie.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

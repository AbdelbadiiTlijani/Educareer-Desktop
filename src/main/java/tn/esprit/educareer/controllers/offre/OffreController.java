package tn.esprit.educareer.controllers.offre;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.educareer.models.Offre_Emploi;
import tn.esprit.educareer.models.Type_Offre;
import tn.esprit.educareer.services.OffreEmploiService;
import tn.esprit.educareer.services.TypeOffreService;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class OffreController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ComboBox<Type_Offre> idtype;

    @FXML
    private TextField titreField;

    @FXML
    private TextField descoffreField;

    @FXML
    private TextField lieuField;

    @FXML
    private TextField salaireField;

    @FXML
    private ListView<Offre_Emploi> offreListView;

    private final TypeOffreService typeOffreService = new TypeOffreService();
    private final OffreEmploiService offreService = new OffreEmploiService();

    public void initialize() {
        afficherOffres();
        idtype.setVisible(false); // Cacher le ComboBox si tu ne veux pas l’afficher
    }

    private void afficherOffres() {
        List<Offre_Emploi> offres = offreService.getAll();
        offreListView.getItems().setAll(offres);

        offreListView.setCellFactory(new Callback<ListView<Offre_Emploi>, ListCell<Offre_Emploi>>() {
            @Override
            public ListCell<Offre_Emploi> call(ListView<Offre_Emploi> param) {
                return new ListCell<Offre_Emploi>() {
                    private final Label offreLabel = new Label();
                    private final Button supprimerBtn = new Button("Supprimer");
                    private final Button updateBtn = new Button("Modifier");
                    private final HBox hBox = new HBox(10, offreLabel, updateBtn, supprimerBtn);

                    {
                        supprimerBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");
                        updateBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");

                        supprimerBtn.setOnAction(event -> {
                            Offre_Emploi selected = getItem();
                            if (selected != null) {
                                offreService.supprimer(selected);
                                afficherOffres();
                            }
                        });

                        updateBtn.setOnAction(event -> {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/offre/UpdateOffre.fxml"));
                                Parent root = loader.load();
                                UpdateOffre controller = loader.getController();
                                controller.setOffreToEdit(getItem());

                                Stage stage = new Stage();
                                stage.setTitle("Modifier Offre");
                                stage.setScene(new Scene(root));
                                stage.show();

                            } catch (IOException e) {
                                System.out.println("Erreur de navigation : " + e.getMessage());
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Offre_Emploi offre, boolean empty) {
                        super.updateItem(offre, empty);
                        if (empty || offre == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            offreLabel.setText(offre.getTitre() + " - " + offre.getLieu() + " (" + offre.getSalaire() + " DT)");
                            setGraphic(hBox);
                        }
                    }
                };
            }
        });
    }


    @FXML
    public void handleAjouterOffre(ActionEvent event) {
        try {
            String titre = titreField.getText();
            String descoffre = descoffreField.getText();
            String lieu = lieuField.getText();
            double salaire = Double.parseDouble(salaireField.getText());
            Type_Offre typeOffre = idtype.getSelectionModel().getSelectedItem();

            if (typeOffre != null) {
                Offre_Emploi nouvelleOffre = new Offre_Emploi(titre, descoffre, lieu, salaire, typeOffre);
                offreService.ajouter(nouvelleOffre);
                afficherOffres();
                titreField.clear();
                descoffreField.clear();
                lieuField.clear();
                salaireField.clear();
                idtype.getSelectionModel().clearSelection();
            } else {
                System.out.println("Veuillez sélectionner un type d'offre.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Salaire invalide !");
        }
    }

    @FXML
    void handlback(ActionEvent event) throws IOException {
        navigateToPage(event, "/User/AdminDashboard.fxml");
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
    void handleAjouterType(ActionEvent event) throws IOException {
        navigateToPage(event, "/offre/AjouterTypeOffre.fxml");
    }

    @FXML
    void handleListType(ActionEvent event) throws IOException {
        navigateToPage(event, "/offre/TypeList.fxml");

    }

    @FXML
    void handleajouteroffree(ActionEvent event) throws IOException {
        navigateToPage(event, "/offre/AjouterOffre.fxml");
    }


}

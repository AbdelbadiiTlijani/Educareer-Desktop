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

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;





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
    private TextField searchField;

    @FXML
    private ComboBox<String> salaireSortComboBox;

    @FXML
    private ListView<Offre_Emploi> offreListView;

    private final TypeOffreService typeOffreService = new TypeOffreService();
    private final OffreEmploiService offreService = new OffreEmploiService();

    public void initialize() {
        afficherOffres();
        idtype.setVisible(false); // Cacher le ComboBox si tu ne veux pas l’afficher

        // Ajout du listener pour recherche dynamique
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            rechercherOffresParTitre(newValue);
        });

        salaireSortComboBox.getItems().addAll("Salaire Croissant", "Salaire Décroissant");
        salaireSortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                trierOffresParSalaire(newVal);
            }
        });

    }

    private void rechercherOffresParTitre(String titreRecherche) {
        List<Offre_Emploi> toutesLesOffres = offreService.getAll();
        List<Offre_Emploi> resultats = toutesLesOffres.stream()
                .filter(offre -> offre.getTitre().toLowerCase().contains(titreRecherche.toLowerCase()))
                .toList();
        offreListView.getItems().setAll(resultats);
    }

    private void trierOffresParSalaire(String critere) {
        List<Offre_Emploi> offres = offreService.getAll();

        if (critere.equals("Salaire Croissant")) {
            offres.sort((o1, o2) -> Double.compare(o1.getSalaire(), o2.getSalaire()));
        } else if (critere.equals("Salaire Décroissant")) {
            offres.sort((o1, o2) -> Double.compare(o2.getSalaire(), o1.getSalaire()));
        }

        offreListView.getItems().setAll(offres);
    }



    private void afficherOffres() {
        List<Offre_Emploi> offres = offreService.getAll();
        offreListView.getItems().setAll(offres);

        offreListView.setCellFactory(param -> new ListCell<Offre_Emploi>() {
            private final Label titreLabel = new Label();
            private final Label descriptionLabel = new Label();
            private final Label postulationsLabel = new Label();  // Nouveau label pour afficher le nombre de postulations
            private final Button modifierBtn = new Button("Modifier");
            private final Button supprimerBtn = new Button("Supprimer");
            private final VBox vboxText = new VBox(titreLabel, descriptionLabel, postulationsLabel);
            private final HBox hBox = new HBox(20, vboxText, modifierBtn, supprimerBtn);

            {
                titreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                descriptionLabel.setStyle("-fx-text-fill: #777;");
                postulationsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");  // Style pour le nombre de postulations
                modifierBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
                supprimerBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
                vboxText.setSpacing(5);
                hBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10;");
                hBox.setSpacing(20);

                modifierBtn.setOnAction(event -> {
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

                supprimerBtn.setOnAction(event -> {
                    Offre_Emploi selected = getItem();
                    if (selected != null) {
                        offreService.supprimer(selected);
                        afficherOffres();
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
                    titreLabel.setText(offre.getTitre() + " - " + offre.getLieu() + " (" + offre.getSalaire() + " DT)");
                    descriptionLabel.setText(offre.getDescoffre());
                    // Affiche le nombre de postulations
                    postulationsLabel.setText("Postulations: " + offre.getNombre_postulations());
                    setGraphic(hBox);
                }
            }
        });
    }





    @FXML
    public <Int> void handleAjouterOffre(ActionEvent event) {
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


    public void handleListPostulations(ActionEvent event) throws IOException {
        navigateToPage(event, "/offre/PostList.fxml");

    }
}

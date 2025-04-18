package tn.esprit.educareer.controllers.offre;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Offre_Emploi;
import tn.esprit.educareer.models.Type_Offre;
import tn.esprit.educareer.services.OffreEmploiService;
import tn.esprit.educareer.services.TypeOffreService;

import java.io.IOException;
import java.net.URL;

public class UpdateOffre {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField descoffre;

    @FXML
    private TextField lieu;

    @FXML
    private ListView<?> offreListView;

    @FXML
    private TextField salaire;

    @FXML
    private TextField searchField;

    @FXML
    private TextField titre;

    @FXML
    private ComboBox<Type_Offre> idtype; // ComboBox pour les types d'offres

    private OffreEmploiService os = new OffreEmploiService();
    private TypeOffreService typeOffreService = new TypeOffreService(); // Service pour charger les types d'offres

    private Offre_Emploi selectedOffre;

    @FXML
    void handleBackButton(ActionEvent event) throws IOException {

        navigateToPage(event, "/offre/OffreList.fxml");
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

    // Méthode pour charger les types d'offres dans le ComboBox
    private void chargerTypesOffres() {
        idtype.getItems().setAll(typeOffreService.getAll()); // Charger les types d'offres
    }

    @FXML
    public void initialize() {
        chargerTypesOffres(); // Charger les types d'offres au démarrage
    }

    public void setOffreToEdit(Offre_Emploi offre) {
        this.selectedOffre = offre;

        // Pré-remplir les champs du formulaire
        titre.setText(offre.getTitre());
        lieu.setText(offre.getLieu());
        salaire.setText(String.valueOf(offre.getSalaire()));
        descoffre.setText(offre.getDescoffre());

        // Pré-sélectionner le type d'offre dans le ComboBox
        idtype.setValue(offre.getTypeOffre()); // Assurez-vous que getTypeOffre() existe dans Offre_Emploi
    }

    @FXML
    public void save(ActionEvent event) throws IOException {
        // Met à jour l'objet existant
        selectedOffre.setTitre(titre.getText());
        selectedOffre.setDescoffre(descoffre.getText());
        selectedOffre.setLieu(lieu.getText());
        selectedOffre.setSalaire(Double.parseDouble(salaire.getText()));

        // Si tu veux aussi modifier le type d'offre (si ComboBox idtype existe)
        if (idtype != null && idtype.getValue() != null) {
            selectedOffre.setTypeOffre(idtype.getValue());
        }

        os.modifier(selectedOffre);

        // Fermer la fenêtre
        Parent root = FXMLLoader.load(getClass().getResource("/offre/OffreList.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);

    }
}

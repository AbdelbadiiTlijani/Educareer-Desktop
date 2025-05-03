package tn.esprit.educareer.controllers.offre;

import tn.esprit.educareer.services.SalaryAPI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

public class AjouterOffre {

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

    // Méthode pour charger les types d'offres dans le ComboBox
    private void chargerTypesOffres() {
        idtype.getItems().setAll(typeOffreService.getAll()); // Charger les types d'offres dans le ComboBox
    }

    @FXML
    public void initialize() {
        chargerTypesOffres();
        // Quand on change de type d'offre
        idtype.valueProperty().addListener((obs, oldVal, newVal) -> {
            autofillSalary();
        });

        // Quand on change de lieu
        lieu.textProperty().addListener((observable, oldValue, newValue) -> {
            autofillSalary();
        });
    }

    // Méthode pour auto-compléter le salaire
    private void autofillSalary() {
        Type_Offre selectedType = idtype.getValue();
        String lieuText = lieu.getText();

        if (selectedType != null && lieuText != null && !lieuText.trim().isEmpty()) {
            Double estimatedSalary = SalaryAPI.getAverageSalary(selectedType.getCategorie(), lieuText);
            if (estimatedSalary != null) {
                salaire.setText(String.valueOf((int) Math.round(estimatedSalary)));
            } else {
                System.out.println("Pas de salaire estimé pour ce poste à cet endroit.");
            }
        }
    }

    @FXML
    void save(ActionEvent event) throws IOException {
        // Récupérer les valeurs du formulaire
        String titreText = titre.getText();
        String description = descoffre.getText();
        String lieuText = lieu.getText();
        String salaireText = salaire.getText();

        // Vérification des champs obligatoires
        if (titreText == null || titreText.trim().length() < 2 ||
                description == null || description.trim().length() < 2 ||
                lieuText == null || lieuText.trim().length() < 2 ||
                salaireText == null || salaireText.trim().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants ou invalides");
            alert.setHeaderText(null);
            alert.setContentText("Tous les champs doivent contenir au moins 2 caractères et ne doivent pas être vides.");
            alert.showAndWait();
            return;
        }

        double salaireValue;
        try {
            salaireValue = Double.parseDouble(salaireText);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Salaire invalide");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez saisir un salaire valide.");
            alert.showAndWait();
            return;
        }

        // Récupérer la catégorie sélectionnée
        Type_Offre selectedType = idtype.getValue();

        // Vérifier si un type est sélectionné
        if (selectedType == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Type d'offre manquant");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une catégorie.");
            alert.showAndWait();
            return;
        }

        // Créer l'objet Offre_Emploi avec type
        Offre_Emploi newOffre = new Offre_Emploi(titreText, description, lieuText, salaireValue, selectedType);

        // Ajouter via le service
        os.ajouter(newOffre);

        // Revenir à la liste des offres
        navigateToPage(event, "/offre/OffreList.fxml");
    }

    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/offre/OffreList.fxml");
    }

    // Méthode pour naviguer vers une autre page
    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        root = FXMLLoader.load(getClass().getResource(path));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root , 1000 , 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}

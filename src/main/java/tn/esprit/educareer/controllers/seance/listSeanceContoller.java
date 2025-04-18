package tn.esprit.educareer.controllers.seance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Seance;
import tn.esprit.educareer.services.ServiceSeance;
import tn.esprit.educareer.utils.UserSession;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class listSeanceContoller implements Initializable {

    private final ServiceSeance serviceSeance = new ServiceSeance();
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private ListView<Seance> seanceListView;
    private ObservableList<Seance> allSeances;
    @FXML
    private Button addSeanceButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger toutes les séances
        List<Seance> list = serviceSeance.getAll();
        allSeances = FXCollections.observableArrayList(list);
        seanceListView.setItems(allSeances);

        // Personnaliser les cellules
        seanceListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Seance seance, boolean empty) {
                super.updateItem(seance, empty);
                if (empty || seance == null) {
                    setGraphic(null);
                } else {
                    HBox container = new HBox(10);
                    container.setStyle("-fx-padding: 10px; -fx-background-color: white; -fx-background-radius: 5px;");

                    VBox info = new VBox(5);
                    Label titreLabel = new Label("Titre: " + seance.getTitre());
                    Label descLabel = new Label("Description: " + seance.getDescription());
                    Label dateLabel = new Label("Date: " + seance.getDate_heure().toString());
                    Label dureeLabel = new Label("Durée: " + seance.getDuree() + " minutes");

                    titreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    info.getChildren().addAll(titreLabel, descLabel, dateLabel, dureeLabel);

                    // Bouton Supprimer
                    Button deleteBtn = new Button("Supprimer");
                    deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                    deleteBtn.setOnAction(event -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation de suppression");
                        alert.setHeaderText("Supprimer la séance");
                        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette séance ?");

                        if (alert.showAndWait().get() == ButtonType.OK) {
                            serviceSeance.supprimer(seance);
                            allSeances.remove(seance);
                        }
                    });

                    // Bouton Modifier
                    Button updateBtn = new Button("Modifier");
                    updateBtn.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white; -fx-font-weight: bold;");
                    updateBtn.setOnAction(event -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/seance/updateSeance.fxml"));
                            Parent root = loader.load();

                            updateSeanceController controller = loader.getController();
                            controller.initData(seance); // transmettre la séance à modifier

                            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            stage.setScene(new Scene(root));
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    container.getChildren().addAll(info, updateBtn, deleteBtn);
                    setGraphic(container);
                }
            }
        });
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        root = FXMLLoader.load(fxmlLocation);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    private void handleBackButton() {
        System.out.println("Retour cliqué !");
    }

    @FXML
    void handleAddSeanceButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/seance/ajouterSeance.fxml");
    }

    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        if (UserSession.getInstance().getCurrentUser().getRole().equals("admin"))
            navigateToPage(event, "/User/AdminDashboard.fxml");
        else if (UserSession.getInstance().getCurrentUser().getRole().equals("formateur"))
            navigateToPage(event, "/User/FormateurDashboard.fxml");
    }

}

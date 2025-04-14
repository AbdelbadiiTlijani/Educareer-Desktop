package tn.esprit.educareer.controllers.Event;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Event;
import tn.esprit.educareer.services.ServiceEvent;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EventController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ListView<Event> eventListView;  // Assurez-vous que le type est "Event" et pas générique

    @FXML
    private TextField searchField;

    private ServiceEvent serviceEvent;

    // Constructeur
    public EventController() {
        serviceEvent = new ServiceEvent();  // Initialise ton service
    }

    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/User/AdminDashboard.fxml");
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

    @FXML
    void GestionEvent(ActionEvent event) {
        // Cette méthode peut être utilisée pour traiter d'autres événements si nécessaire.
    }

    private void loadEvents() {
        // Récupérer la liste des événements depuis le service
        List<Event> events = serviceEvent.getAll();

        // Assurer que la ListView est vide avant de remplir
        eventListView.getItems().clear();

        // Ajouter tous les événements à la ListView
        eventListView.getItems().addAll(events);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Appeler la méthode pour récupérer les événements et les afficher dans le ListView
        loadEvents();

        // Personnaliser l'affichage des éléments dans la ListView
        eventListView.setCellFactory(param -> new ListCell<Event>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Créer un VBox ou HBox pour afficher l'événement
                    VBox vbox = new VBox();
                    vbox.setSpacing(5);
                    vbox.setStyle("-fx-border-color: #d3d3d3; -fx-padding: 10px; -fx-background-color: #f9f9f9;");

                    // Titre
                    Label titreLabel = new Label(event.getTitre());
                    titreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    // Date
                    Label dateLabel = new Label("Date: " + event.getDate().toString());
                    dateLabel.setStyle("-fx-font-size: 12px;");

                    // Lieu
                    Label lieuLabel = new Label("Lieu: " + event.getLieu());
                    lieuLabel.setStyle("-fx-font-size: 12px;");

                    // Nombre de places
                    Label nbrPlacesLabel = new Label("Places disponibles: " + event.getNbrPlace());
                    nbrPlacesLabel.setStyle("-fx-font-size: 12px;");

                    // Ajouter les labels au VBox
                    vbox.getChildren().addAll(titreLabel, dateLabel, lieuLabel, nbrPlacesLabel);

                    // Créer un HBox pour les boutons Modifier et Supprimer
                    HBox actionButtons = new HBox();
                    actionButtons.setSpacing(10);

                    // Bouton Modifier
                    Button modifyButton = new Button("Modifier");
                    modifyButton.setOnAction(e -> handleModifier(event));

                    // Bouton Supprimer
                    Button deleteButton = new Button("Supprimer");
                    deleteButton.setOnAction(e -> handleSupprimer(event));

                    // Ajouter les boutons à l'HBox
                    actionButtons.getChildren().addAll(modifyButton, deleteButton);

                    // Ajouter le VBox et l'HBox au ListCell
                    vbox.getChildren().add(actionButtons);

                    // Mettre en place le VBox comme contenu de la cellule
                    setGraphic(vbox);
                }
            }
        });
    }

    // Méthode pour rediriger vers la page de modification
    private void handleModifier(Event event) {
        try {
            // Charger le formulaire de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event/EditEvent.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la page de modification
            EditEventController editEventController = loader.getController();

            // Passer l'événement à modifier au contrôleur
            editEventController.setEvent(event);

            // Ouvrir la fenêtre de modification
            Stage stage = (Stage) eventListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour supprimer l'événement
    private void handleSupprimer(Event event) {
        serviceEvent.supprimer(event);  // Appeler la méthode pour supprimer l'événement
        loadEvents();  // Recharger la liste après suppression
    }
}

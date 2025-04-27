package tn.esprit.educareer.controllers.cours;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Cours;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ServiceCours;
import tn.esprit.educareer.services.ServiceUser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;


public class listCoursController {
    private final ServiceCours serviceCours = new ServiceCours();

    @FXML
    private ListView<Cours> coursListView;

    @FXML
    private ComboBox<String> roleFilter;

    @FXML
    private TextField searchField;

    @FXML
    private Button viewStatCours;
    @FXML
    private Button viewBackButton;


    private Stage stage;
    private Scene scene;

    ServiceUser serviceUser = new ServiceUser();
    @FXML
    public void initialize() {

        roleFilter.getItems().add("Tous");
        roleFilter.getItems().addAll(serviceUser.getAll().stream().map(User::getNom).toList());
        roleFilter.setValue("Tous");

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filterCoursList();
        });

        roleFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            filterCoursList();
        });


        ObservableList<Cours> observableList = FXCollections.observableArrayList(serviceCours.getAll());
        coursListView.setItems(observableList);

        coursListView.setCellFactory(listView -> new ListCell<>() {
            private final Label nomLabel = new Label();
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final Button pdfButton = new Button("PDF");

            private final HBox hbox = new HBox(10, nomLabel, editButton, deleteButton, pdfButton);

            {
                editButton.getStyleClass().add("btn-modifier");
                deleteButton.getStyleClass().add("btn-supprimer");
                pdfButton.getStyleClass().add("btn-pdf");

                editButton.setOnAction(this::handleEdit);
                deleteButton.setOnAction(this::handleDelete);
                pdfButton.setOnAction(this::handlePdf);
            }

            @Override
            protected void updateItem(Cours cours, boolean empty) {
                super.updateItem(cours, empty);
                if (empty || cours == null) {
                    setGraphic(null);
                } else {
                    nomLabel.setText(cours.getNom());
                    nomLabel.setStyle("-fx-font-family: bold; -fx-font-size: 20px;-fx-text-fill: #2c3e50;");
                    setGraphic(hbox);
                }
            }

            private void handleEdit(ActionEvent event) {
                Cours selected = getItem();
                if (selected != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cours/updateCours.fxml"));
                        Parent root = loader.load();

                        updateCoursController controller = loader.getController();
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
                Cours selected = getItem();
                if (selected != null) {
                    serviceCours.supprimer(selected);
                    getListView().getItems().remove(selected);
                }
            }

            private void handlePdf(ActionEvent event) {
                Cours selected = getItem();
                if (selected != null && selected.getDocument() != null) {
                    File sourceFile = new File(selected.getDocument());
                    if (sourceFile.exists()) {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Enregistrer le document PDF");
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
                        fileChooser.setInitialFileName(sourceFile.getName());

                        // Afficher la boîte de dialogue d'enregistrement
                        File destinationFile = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());

                        if (destinationFile != null) {
                            try {
                                java.nio.file.Files.copy(
                                        sourceFile.toPath(),
                                        destinationFile.toPath(),
                                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                                );
                                System.out.println("Fichier téléchargé avec succès.");
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("Erreur lors du téléchargement du fichier.");
                            }
                        }
                    } else {
                        System.out.println("Le fichier source est introuvable.");
                    }
                }
            }
        });

    }

    private void filterCoursList() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedFormatteur = roleFilter.getValue();

        List<Cours> filtered = serviceCours.getAll().stream()
                .filter(cours -> cours.getNom().toLowerCase().contains(searchText))
                .filter(cours -> selectedFormatteur.equals("Tous") || cours.getUser().getNom().equals(selectedFormatteur))
                .toList();

        coursListView.getItems().setAll(filtered);
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

    @FXML
    void handleStatCours(ActionEvent event) throws IOException {
        navigateToPage(event, "/cours/statCours.fxml");
    }
}

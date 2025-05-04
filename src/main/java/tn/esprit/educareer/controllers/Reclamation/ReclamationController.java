package tn.esprit.educareer.controllers.Reclamation;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.educareer.models.Reclamation;
import tn.esprit.educareer.models.TypeReclamation;
import tn.esprit.educareer.services.ReclamationService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ReclamationController {

    private Stage stage;
    private Scene scene;
    private Parent root;


    @FXML
    private Button pdfButton, handlback;
    @FXML
    private ComboBox<TypeReclamation> idtype;
    @FXML
    private ListView<Reclamation> reclamationListView;
    @FXML
    private TextField searchField;

    private final ReclamationService reclamationService = new ReclamationService();

    @FXML
    public void initialize() {
        List<TypeReclamation> types = reclamationService.getAllTypes();
        ObservableList<TypeReclamation> typesList = FXCollections.observableArrayList(types);
        idtype.setItems(typesList);

        afficherReclamations();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAndDisplayReclamations();
        });

        idtype.setOnAction(event -> {
            filterAndDisplayReclamations();
        });

        // ➕ Ajout : Affichage de l'élément sélectionné au clic
        reclamationListView.setOnMouseClicked(event -> {
            Reclamation selectedItem = reclamationListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                System.out.println("Item sélectionné : " + selectedItem.getSujet());
            }
        });
    }

    private void afficherReclamations() {
        List<Reclamation> reclamations = reclamationService.getAll();
        updateListView(reclamations);
    }

    private void filterAndDisplayReclamations() {
        String searchKeyword = searchField.getText().toLowerCase();
        TypeReclamation selectedType = idtype.getSelectionModel().getSelectedItem();

        List<Reclamation> filteredList = reclamationService.getAll().stream()
                .filter(r -> (searchKeyword.isEmpty() || r.getSujet().toLowerCase().contains(searchKeyword) || r.getDescription().toLowerCase().contains(searchKeyword)))
                .filter(r -> (selectedType == null || r.getTypeReclamation().getId() == selectedType.getId()))
                .toList();

        updateListView(filteredList);
    }

    private void updateListView(List<Reclamation> reclamations) {
        ObservableList<Reclamation> reclamationsList = FXCollections.observableArrayList(reclamations);
        reclamationListView.setItems(reclamationsList);

        reclamationListView.setCellFactory(param -> new ListCell<>() {
            private final Label sujetLabel = new Label();
            private final Label descriptionLabel = new Label();
            private final Label dateLabel = new Label();
            private final Button modifierBtn = new Button("✎ Modifier");
            private final Button supprimerBtn = new Button("✖ Supprimer");
            private final HBox buttonBox = new HBox(10, modifierBtn, supprimerBtn);
            private final VBox contentBox = new VBox(5, sujetLabel, descriptionLabel, dateLabel, buttonBox);
            private final HBox rootBox = new HBox(contentBox);

            {
                rootBox.setSpacing(10);
                rootBox.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-background-radius: 10;");
                buttonBox.setStyle("-fx-alignment: center-left;");
                modifierBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
                supprimerBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");

                rootBox.setOnMouseEntered(e -> rootBox.setStyle("-fx-padding: 10; -fx-background-color: #e0e0e0; -fx-background-radius: 10;"));
                rootBox.setOnMouseExited(e -> rootBox.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-background-radius: 10;"));

                supprimerBtn.setOnAction(event -> {
                    Reclamation selected = getItem();
                    if (selected != null) {
                        reclamationService.supprimer(selected);
                        filterAndDisplayReclamations();
                    }
                });

                modifierBtn.setOnAction(event -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/ModifierReclamation.fxml"));
                        Parent root = loader.load();
                        ModifierReclamation controller = loader.getController();
                        controller.setReclamationToEdit(getItem());

                        Stage currentStage = (Stage) modifierBtn.getScene().getWindow();
                        currentStage.setTitle("Modifier Réclamation");
                        currentStage.setScene(new Scene(root));
                        currentStage.show();
                    } catch (IOException e) {
                        System.out.println("Erreur de navigation : " + e.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Reclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);
                if (empty || reclamation == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    sujetLabel.setText("🔹 " + reclamation.getSujet() + " (" + reclamation.getTypeReclamation().getNom() + ")");
                    descriptionLabel.setText(resumeTexte(reclamation.getDescription(), 50));
                    dateLabel.setText("🕒 Créé le : " + (reclamation.getCreatedAt() != null ? reclamation.getCreatedAt().toLocalDate() : "N/A"));
                    setGraphic(rootBox);
                }
            }
        });
    }

    private String resumeTexte(String texte, int maxLength) {
        if (texte.length() <= maxLength) return texte;
        return texte.substring(0, maxLength) + "...";
    }

    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/User/AdminDashboard.fxml");
    }

    @FXML
    void handleAjouterReclamation(ActionEvent event) throws IOException {
        navigateToPage(event, "/Reclamation/AjouterReclamation.fxml");
    }

    @FXML
    void handleListType(ActionEvent event) throws IOException {
        navigateToPage(event, "/Reclamation/TypeReclamation.fxml");
    }

    @FXML
    void handleAjouterType(ActionEvent event) throws IOException {
        navigateToPage(event, "/Reclamation/AjouterTypeReclamation.fxml");
    }

    @FXML
    void handleGeneratePDF(ActionEvent event) {
        Reclamation selectedReclamation = reclamationListView.getSelectionModel().getSelectedItem();
        if (selectedReclamation != null) {
            try {
                Document document = new Document();
                String fileName = "reclamation_" + selectedReclamation.getId() + ".pdf";
                PdfWriter.getInstance(document, new FileOutputStream(fileName));
                document.open();

                document.add(new Paragraph("Réclamation PDF"));
                document.add(new Paragraph("Sujet: " + selectedReclamation.getSujet()));
                document.add(new Paragraph("Description: " + selectedReclamation.getDescription()));
                document.add(new Paragraph("Type: " + selectedReclamation.getTypeReclamation().getNom()));
                document.add(new Paragraph("Créé le: " + selectedReclamation.getCreatedAt().toLocalDate()));

                document.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("PDF généré");
                alert.setHeaderText("Succès");
                alert.setContentText("Le fichier PDF a été généré avec succès :\n" + fileName);
                alert.showAndWait();

                java.awt.Desktop.getDesktop().open(new java.io.File(fileName));

                // Envoi de mail après succès


            } catch (DocumentException | IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Échec de la génération du PDF");
                alert.setContentText("Une erreur s'est produite lors de la génération ou l'ouverture du fichier PDF.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setHeaderText("Aucune réclamation sélectionnée");
            alert.setContentText("Veuillez sélectionner une réclamation avant de générer le PDF.");
            alert.showAndWait();
        }
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
}

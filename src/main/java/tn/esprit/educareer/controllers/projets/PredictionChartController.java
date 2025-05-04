package tn.esprit.educareer.controllers.projets;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PredictionChartController {

    @FXML
    private LineChart<String, Number> lineChart; // Déclaration du graphique

    @FXML
    private CategoryAxis xAxis; // Déclaration de l'axe des catégories
    @FXML
    private NumberAxis yAxis;  // Déclaration de l'axe des nombres

    @FXML
    private Button backButton;  // Déclaration du bouton retour

    @FXML
    private void initialize() {
        // Vérifie que les axes sont initialisés
        if (xAxis != null && yAxis != null) {
            // Charger les données depuis Pred.json
            loadDataFromJson();

            // Ajout d'une rotation des labels sur l'axe des X pour plus de lisibilité (optionnel)
            xAxis.setTickLabelRotation(45); // Faire pivoter les labels à 45 degrés

            // Ajouter une action pour le bouton retour

        } else {
            System.out.println("Erreur : Les axes sont null !");
        }
    }

    private void loadDataFromJson() {
        // Le chemin du fichier JSON
        File jsonFile = new File("C:/Users/Mega-PC/Desktop/Integration-Pi (2)/Integration-Pi/templates/chart/pred.json");

        // Utiliser Jackson pour parser le fichier JSON
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Charger et lire le fichier JSON
            JsonNode rootNode = objectMapper.readTree(jsonFile);

            // Extraire les données des colonnes et des valeurs
            JsonNode dataNode = rootNode.path("data");
            JsonNode columnsNode = rootNode.path("columns");

            // Vérifier si les colonnes sont bien définies
            if (columnsNode.isArray() && dataNode.isArray()) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Prévisions");

                // La colonne ds (date) devrait être la première colonne
                int dateColumnIndex = -1;
                int valueColumnIndex = -1;

                // Recherche de l'index des colonnes "ds" et "yhat"
                for (int i = 0; i < columnsNode.size(); i++) {
                    if (columnsNode.get(i).asText().equals("ds")) {
                        dateColumnIndex = i;
                    }
                    if (columnsNode.get(i).asText().equals("yhat")) {
                        valueColumnIndex = i;
                    }
                }

                // Si les colonnes "ds" et "yhat" existent
                if (dateColumnIndex != -1 && valueColumnIndex != -1) {
                    // Parcourir les données
                    for (JsonNode dataItem : dataNode) {
                        // Extraire la date (en timestamp) et la valeur
                        long timestamp = dataItem.get(dateColumnIndex).asLong();
                        double value = dataItem.get(valueColumnIndex).asDouble();

                        // Convertir le timestamp en date
                        String date = new java.text.SimpleDateFormat("yyyy-MM-dd")
                                .format(new java.util.Date(timestamp));

                        // Ajouter les données à la série
                        series.getData().add(new XYChart.Data<>(date, value));
                    }

                    // Ajouter la série de données au graphique
                    lineChart.getData().add(series);
                } else {
                    showError("Erreur de données", "Les colonnes 'ds' et 'yhat' sont manquantes dans le fichier JSON.");
                }
            } else {
                showError("Erreur de format", "Le fichier JSON ne contient pas les données attendues.");
            }

        } catch (IOException e) {
            showError("Erreur de lecture", "Erreur lors de la lecture du fichier JSON.");
            e.printStackTrace();
        }
    }


    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        navigateToPage(event, "/User/AdminDashboard.fxml");
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) throw new IOException("FXML file not found at: " + path);
        Parent root = FXMLLoader.load(fxmlLocation);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}

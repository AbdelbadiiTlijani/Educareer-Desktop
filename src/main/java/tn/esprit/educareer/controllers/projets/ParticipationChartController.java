package tn.esprit.educareer.controllers.projets;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.stage.Stage;
import tn.esprit.educareer.services.ServiceParticipationProjet;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Map;

public class ParticipationChartController {

    @FXML
    private LineChart<String, Number> lineChart;

    private ServiceParticipationProjet participationProjetService = new ServiceParticipationProjet();

    @FXML
    public void initialize() {
        Map<LocalDate, Integer> participationData = participationProjetService.getParticipantsCountByDate();

        // Créer une nouvelle série de données pour le graphique
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Participants par jour");

        // Trie les dates par ordre croissant
        participationData.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // Trie par date
                .forEach(entry -> {
                    // Ajoute les données triées à la série
                    series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
                });

        // Ajoute la série au graphique
        lineChart.getData().add(series);
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

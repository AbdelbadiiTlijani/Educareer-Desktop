package tn.esprit.educareer.controllers.cours;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import tn.esprit.educareer.services.ServiceCours;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class statCoursController implements Initializable {

    ServiceCours serviceCours = new ServiceCours();
    @FXML
    private PieChart pieChart;
    @FXML
    private BarChart<String, Number> barChart;
    private Stage stage;
    private Scene scene;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pieChart.getStylesheets().add(getClass().getResource("/cours/statCours.css").toExternalForm());

        loadPieChartData();
        loadChartData();
    }


    private void loadPieChartData() {
        Map<String, Integer> stats = serviceCours.CoursParCategorie();
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        int total = stats.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
            data.add(slice);
        }

        pieChart.setData(data);
        pieChart.setTitle("Répartition des catégories de cours");
        for (PieChart.Data dataPie : pieChart.getData()) {
            double percentage = dataPie.getPieValue() / total * 100;

            // Placer le label au centre du secteur
            dataPie.getNode().setOnMouseEntered(e -> dataPie.getNode().setStyle("-fx-pie-color: #ff6347;"));
            dataPie.getNode().setOnMouseExited(e -> dataPie.getNode().setStyle(""));
            dataPie.setName(dataPie.getName() + String.format("  %.1f%%", percentage));
        }

    }


    private void loadChartData() {
        Map<String, Integer> stats = serviceCours.formateurParCategorie();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Formateurs par Catégorie");

        int colorIndex = 0;
        String[] colors = {"#4CAF50", "#2196F3", "#FFC107", "#FF5722", "#9C27B0", "#00BCD4"};

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            series.getData().add(data);

            final int currentIndex = colorIndex % colors.length;
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: " + colors[currentIndex] + ";");
                }
            });
            colorIndex++;
        }

        barChart.getData().clear();
        barChart.getData().add(series);
        barChart.setTitle("Répartition des formatteur par catégorie");

    }


    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/cours/listCours.fxml");
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


}

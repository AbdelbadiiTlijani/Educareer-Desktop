package tn.esprit.educareer.controllers.cours;

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
import javafx.util.Callback;
import tn.esprit.educareer.models.Cours;
import tn.esprit.educareer.services.ServiceCours;

import java.io.IOException;
import java.net.URL;

public class listCoursController {

    @FXML
    private ListView<Cours> coursListView;

    private final ServiceCours serviceCours = new ServiceCours();
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button ViewCours;

    @FXML
    void handleAddCours(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cours/ajouterCours.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);
            Stage stage = (Stage) ViewCours.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getCause());
        }
    }


    @FXML
    private Button ViewCategorieCours;

    @FXML
    void handleAddCategorieCours(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/categorieCours/listCategorieCours.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);
            Stage stage = (Stage) ViewCategorieCours.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    @FXML
    private Button ViewSeance;


    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setResizable(true);
        alert.setHeaderText("Page Load Failed");
        alert.setContentText(content);
        alert.show();
    }


    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/User/AdminDashboard.fxml");
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) throw new IOException("FXML file not found at: " + path);
        root = FXMLLoader.load(fxmlLocation);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}

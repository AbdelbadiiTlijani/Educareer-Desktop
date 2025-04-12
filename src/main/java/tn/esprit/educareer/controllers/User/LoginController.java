package tn.esprit.educareer.controllers.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField emailField;

    @FXML
    private Button backButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    void handleLogin(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        backButton.setOnAction(event -> goBack());
    }
    private void goBack() {
        try {
            // Charger l'écran précédent (ajustez le chemin selon votre structure)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            VBox root = loader.load();

            // Obtenir la scène actuelle
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Définir la nouvelle scène
            Scene scene = new Scene(root, 1200, 800);;
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

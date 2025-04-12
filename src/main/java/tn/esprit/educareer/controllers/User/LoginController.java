package tn.esprit.educareer.controllers.User;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import org.mindrot.jbcrypt.BCrypt;
import tn.esprit.educareer.utils.MyConnection;
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
import java.sql.Connection;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField emailField;

    @FXML
    private Button backButton;

    @FXML
    private PasswordField passwordField;
    private Connection cnx;

    public LoginController() {
        cnx = MyConnection.getInstance().getCnx(); }



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
            Scene scene = new Scene(root, 1000, 700);;
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both email and password.");
            return;
        }

        try {
            // Query user from database
            String query = "SELECT * FROM user WHERE email = ?";
            PreparedStatement pst = cnx.prepareStatement(query);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("mdp");
                String role = rs.getString("role");

                // Check if the stored hash starts with $2y$ (Symfony format)
                if (storedHash.startsWith("$2y$")) {
                    // Convert $2y$ to $2a$ for BCrypt.checkpw compatibility
                    storedHash = "$2a$" + storedHash.substring(4);
                }

                // Verify password
                if (BCrypt.checkpw(password, storedHash)) {
                    // Login successful - redirect based on role
                    redirectBasedOnRole(role);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid email or password.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Error", "User not found.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error checking credentials: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }

    }
    private void redirectBasedOnRole(String role) throws IOException {
        String fxmlPath;

        switch (role.toLowerCase()) {
            case "admin":
                fxmlPath = "/User/AdminDashboard.fxml";
                break;
            case "formateur":
                fxmlPath = "/tn/esprit/educareer/views/formateur.fxml";
                break;
            case "student":
                fxmlPath = "/tn/esprit/educareer/views/students.fxml";
                break;
            default:
                showAlert(Alert.AlertType.ERROR, "Role Error", "Unknown role: " + role);
                return;
        }

        // Load the appropriate FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Create new scene
        Scene scene = new Scene(root , 1000 , 700);

        // Get the current stage
        Stage stage = (Stage) emailField.getScene().getWindow();

        // Set the new scene
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}

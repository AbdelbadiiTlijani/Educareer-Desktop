package tn.esprit.educareer.controllers.User;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;
import tn.esprit.educareer.utils.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.educareer.utils.UserSession;
import  tn.esprit.educareer.models.User;

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
    private Label emailErrorLabel;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private Label statusLabel;

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

        // Clear error messages when user starts typing
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            emailErrorLabel.setText("");
            highlightField(emailField, false);
            statusLabel.setText("");
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            passwordErrorLabel.setText("");
            highlightField(passwordField, false);
            statusLabel.setText("");
        });
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
        // Clear previous error messages
        clearErrors();

        // Validate input fields
        boolean isValid = validateInput();

        if (!isValid) {
            return; // Don't proceed if validation failed
        }

        String email = emailField.getText().trim();
        String password = passwordField.getText();

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
                    // Create user object and set session
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setPhoto_profil(rs.getString("photo_profil"));
                    
                    // Set the user session
                    UserSession.getInstance().setCurrentUser(user);
                    
                    // Login successful - redirect based on role
                    redirectBasedOnRole(role);
                } else {
                    // Invalid password
                    statusLabel.setText("Mot de passe incorrect");
                    highlightField(passwordField, true);
                }
            } else {
                // User not found
                statusLabel.setText("Utilisateur non trouvé");
                highlightField(emailField, true);
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur de base de données: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            statusLabel.setText("Erreur de navigation: " + e.getMessage());
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
                fxmlPath = "/User/FormateurDashboard.fxml";
                break;
            case "student":
                fxmlPath = "/User/students.fxml";
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
    private boolean validateInput() {
        boolean isValid = true;

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validate email
        if (email.isEmpty()) {
            emailErrorLabel.setText("L'email est requis");
            highlightField(emailField, true);
            isValid = false;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            emailErrorLabel.setText("Format d'email invalide");
            highlightField(emailField, true);
            isValid = false;
        } else {
            highlightField(emailField, false);
        }

        // Validate password
        if (password.isEmpty()) {
            passwordErrorLabel.setText("Le mot de passe est requis");
            highlightField(passwordField, true);
            isValid = false;
        } else {
            highlightField(passwordField, false);
        }

        return isValid;
    }

    private void highlightField(TextField field, boolean isError) {
        if (isError) {
            field.setStyle("-fx-background-color: rgba(30, 41, 59, 0.7); " +
                    "-fx-background-radius: 8px; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-border-color: #ef4444; " + // Red border for error
                    "-fx-border-width: 1px; " +
                    "-fx-text-fill: #f8fafc; " +
                    "-fx-padding: 8px; " +
                    "-fx-font-size: 14px;");
        } else {
            field.setStyle("-fx-background-color: rgba(30, 41, 59, 0.7); " +
                    "-fx-background-radius: 8px; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-border-color: rgba(59, 130, 246, 0.2); " + // Normal border
                    "-fx-border-width: 1px; " +
                    "-fx-text-fill: #f8fafc; " +
                    "-fx-padding: 8px; " +
                    "-fx-font-size: 14px;");
        }
    }

    private void clearErrors() {
        emailErrorLabel.setText("");
        passwordErrorLabel.setText("");
        statusLabel.setText("");
        highlightField(emailField, false);
        highlightField(passwordField, false);
    }

}

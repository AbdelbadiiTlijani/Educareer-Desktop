package tn.esprit.educareer.controllers.User;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.mindrot.jbcrypt.BCrypt;
import tn.esprit.educareer.services.OtpStorage;
import tn.esprit.educareer.utils.MyConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ForgotPasswordController {

    @FXML
    private Button backButton;

    @FXML
    private Label newPasswordErrorLabel;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label otpErrorLabel;

    @FXML
    private TextField otpField;

    @FXML
    private Label statusLabel;

    private final String DEFAULT_STYLE = "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;";
    private final String ERROR_STYLE = "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: #ef4444; -fx-font-weight: bold;";
    private final String SUCCESS_STYLE = "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: #008000; -fx-font-weight: bold;";

    private Connection cnx =MyConnection.getInstance().getCnx();

    private String email;

    public void initialize() {
        clearFields();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleResetPassword(ActionEvent event) {
        clearErrors();

        if (!validateInputs()) {
            return;
        }

        String otp = otpField.getText().trim();
        String newPassword = newPasswordField.getText().trim();

        // Verify OTP
        if (!OtpStorage.verifyOtp(email, otp)) {
            otpErrorLabel.setText("Code OTP invalide ou expiré");
            highlightField(otpField, true);
            return;
        }

        // Update password in database
        try {
            String query = "UPDATE user SET mdp = ? WHERE email = ?";
            PreparedStatement pst = cnx.prepareStatement(query);
            pst.setString(1, hashPassword(newPassword)); // You should hash the password
            pst.setString(2, email);

            int rowsUpdated = pst.executeUpdate();
            pst.close();

            if (rowsUpdated > 0) {
                statusLabel.setText("Mot de passe réinitialisé avec succès");
                statusLabel.setStyle(SUCCESS_STYLE);

                // Remove the used OTP
                OtpStorage.removeOtp(email);

                // Clear fields
                otpField.clear();
                newPasswordField.clear();
                otpErrorLabel.setText("");
                newPasswordErrorLabel.setText("");

                // Redirect to login after a short delay
                redirectToLoginAfterDelay();
            } else {
                statusLabel.setText("Échec de la réinitialisation du mot de passe");
                statusLabel.setStyle(ERROR_STYLE);
            }

        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de la mise à jour du mot de passe");
            statusLabel.setStyle(ERROR_STYLE);
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate OTP
        if (otpField.getText().trim().isEmpty()) {
            otpErrorLabel.setText("Veuillez entrer le code OTP");
            highlightField(otpField, true);
            isValid = false;
        } else if (!otpField.getText().trim().matches("\\d{6}")) {
            otpErrorLabel.setText("Le code OTP doit contenir 6 chiffres");
            highlightField(otpField, true);
            isValid = false;
        } else {
            highlightField(otpField, false);
        }

        // Validate new password
        String password = newPasswordField.getText().trim();
        if (password.isEmpty()) {
            newPasswordErrorLabel.setText("Veuillez entrer un nouveau mot de passe");
            highlightField(newPasswordField, true);
            isValid = false;
        } else if (password.length() < 8) {
            newPasswordErrorLabel.setText("Le mot de passe doit contenir au moins 8 caractères");
            highlightField(newPasswordField, true);
            isValid = false;
        } else if (!password.matches(".*[A-Z].*")) {
            newPasswordErrorLabel.setText("Le mot de passe doit contenir au moins une majuscule");
            highlightField(newPasswordField, true);
            isValid = false;
        } else if (!password.matches(".*[a-z].*")) {
            newPasswordErrorLabel.setText("Le mot de passe doit contenir au moins une minuscule");
            highlightField(newPasswordField, true);
            isValid = false;
        } else if (!password.matches(".*\\d.*")) {
            newPasswordErrorLabel.setText("Le mot de passe doit contenir au moins un chiffre");
            highlightField(newPasswordField, true);
            isValid = false;
        } else {
            highlightField(newPasswordField, false);
        }

        return isValid;
    }

    private void highlightField(TextField field, boolean isError) {
        if (isError) {
            field.setStyle("-fx-background-color: rgba(30, 41, 59, 0.7); -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #ef4444; -fx-border-width: 2px; -fx-text-fill: #f8fafc; -fx-padding: 8px; -fx-font-size: 14px;");
        } else {
            field.setStyle("-fx-background-color: rgba(30, 41, 59, 0.7); -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: rgba(59, 130, 246, 0.2); -fx-border-width: 1px; -fx-text-fill: #f8fafc; -fx-padding: 8px; -fx-font-size: 14px;");
        }
    }

    private void clearErrors() {
        otpErrorLabel.setText("");
        newPasswordErrorLabel.setText("");
        statusLabel.setText("");

        highlightField(otpField, false);
        highlightField(newPasswordField, false);
    }

    private void clearFields() {
        otpField.clear();
        newPasswordField.clear();
        clearErrors();
    }

    private void resetStatusLabelStyleAfterDelay() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            statusLabel.setStyle(DEFAULT_STYLE);
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    private void redirectToLoginAfterDelay() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/Login.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    // This should be replaced with a proper password hashing method
    private String hashPassword(String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(13));
        // Replace $2a$ with $2y$ for Symfony compatibility
        if (hashed.startsWith("$2a$")) {
            hashed = "$2y$" + hashed.substring(4);
        }
        return hashed;
    }
}
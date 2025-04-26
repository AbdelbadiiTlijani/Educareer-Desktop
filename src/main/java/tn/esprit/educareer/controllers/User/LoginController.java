package tn.esprit.educareer.controllers.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.mindrot.jbcrypt.BCrypt;
import tn.esprit.educareer.services.EmailService;
import tn.esprit.educareer.utils.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.educareer.utils.UserSession;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.OtpStorage;




import java.sql.Connection;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
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

    private static final String DEFAULT_STYLE = "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: #ef4444; -fx-font-weight: bold;";


    @FXML
    private Button backButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckBox;

    private Connection cnx;

    // Account lockout related variables
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Remember Me constants
    private static final String REMEMBER_ME_FILE = "rememberMe.txt";
    private static final int REMEMBER_ME_DAYS = 30; // Number of days to remember the user

    public LoginController() {
        cnx = MyConnection.getInstance().getCnx();
        // Ensure account_security table exists
        createAccountSecurityTableIfNotExists();
    }

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

        // Try to load remembered user credentials
        //loadRememberedUser();
    }

    private void loadRememberedUser() {
        String rememberedEmail = getRememberedUser();
        if (rememberedEmail != null) {
            emailField.setText(rememberedEmail);
            rememberMeCheckBox.setSelected(true);
            // Don't auto-fill password for security reasons
        }
    }

    private String getRememberedUser() {
        File file = new File(REMEMBER_ME_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String[] data = reader.readLine().split(",");
                if (data.length == 2) {
                    String email = data[0];
                    LocalDate expirationDate = LocalDate.parse(data[1]);
                    if (LocalDate.now().isBefore(expirationDate)) {
                        return email;
                    } else {
                        clearSavedLogin();
                    }
                }
            } catch (IOException e) {
                System.out.println("Failed to read rememberMe file: " + e.getMessage());
            }
        }
        return null;
    }

    private void saveRememberedUser(String email) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REMEMBER_ME_FILE))) {
            LocalDate expirationDate = LocalDate.now().plusDays(REMEMBER_ME_DAYS);
            writer.write(email + "," + expirationDate);
        } catch (IOException e) {
            System.out.println("Failed to save rememberMe file: " + e.getMessage());
        }
    }

    private void clearSavedLogin() {
        File file = new File(REMEMBER_ME_FILE);
        if (file.exists()) {
            file.delete();
        }
    }




    private void createAccountSecurityTableIfNotExists() {
        try {
            Statement stmt = cnx.createStatement();
            DatabaseMetaData dbm = cnx.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "account_security", null);

            if (!tables.next()) {
                String sql = "CREATE TABLE account_security (" +
                        "email VARCHAR(255) PRIMARY KEY, " +
                        "failed_attempts INT DEFAULT 0, " +
                        "lockout_time DATETIME NULL)";
                stmt.executeUpdate(sql);
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error creating account_security table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            VBox root = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow();

            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleForgotPassword(MouseEvent event) {
        clearErrors();

        boolean isValid = validateInputPassword(); // Checks if email is valid
        if (!isValid) {
            return;
        }

        String email = emailField.getText().trim();

        try {
            // 🔎 Check if user exists in DB
            String query = "SELECT * FROM user WHERE email = ?";
            PreparedStatement pst = cnx.prepareStatement(query);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String otp = generateOTP(); // Generates random 6-digit code
                LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

                // Store OTP in memory
                OtpStorage.storeOtp(email, otp, expiry);

                // Send email using your EmailService
                EmailService emailService = new EmailService(
                        "badi3tlijani12@gmail.com",
                        "dgbk saoi bviw igml"
                );
                String subject = "Code de vérification - Réinitialisation du mot de passe";
                String content = "<h3>Votre code de vérification</h3><p>Code : <b>" + otp + "</b><br>Valide pendant 10 minutes.</p>";
                emailService.sendEmail(email, subject, content);

                statusLabel.setText("Un code de vérification a été envoyé à votre adresse email.");
                statusLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: #008000; -fx-font-weight: bold;");

                // Wait for 2 seconds to show the message, then redirect
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), evt -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/ForgotPassword.fxml"));
                        Parent root = loader.load();

                        // Pass the email to the controller
                        ForgotPasswordController controller = loader.getController();
                        controller.setEmail(email);

                        Scene scene = new Scene(root);
                        Stage stage = (Stage) emailField.getScene().getWindow();
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        statusLabel.setText("Erreur lors du chargement de l'écran de réinitialisation");
                        statusLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: #ef4444; -fx-font-weight: bold;");
                        e.printStackTrace();
                    }
                }));
                timeline.setCycleCount(1);
                timeline.play();

            } else {
                statusLabel.setText("Utilisateur non trouvé");
                statusLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: #ef4444; -fx-font-weight: bold;");
                highlightField(emailField, true);
            }

            rs.close();
            pst.close();

        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de la vérification de l'utilisateur");
            e.printStackTrace();
        }
    }
    private void resetStatusLabelStyleAfterDelay() {
        // Wait for a few seconds (e.g., 3 seconds) and then reset the style to default
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            statusLabel.setStyle(DEFAULT_STYLE);
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }



    @FXML
    void handleLogin(ActionEvent event) {
        clearErrors();

        boolean isValid = validateInput();

        if (!isValid) {
            return;
        }

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Check if account is locked
        try {
            if (isAccountLocked(email)) {
                LocalDateTime unlockTime = getAccountLockoutTime(email);
                statusLabel.setText("Compte bloqué temporairement. Réessayez après " +
                        unlockTime.format(formatter));
                return;
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de la vérification du statut du compte");
            e.printStackTrace();
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
                int status = rs.getInt("status");

                // Check if the stored hash starts with $2y$ (Symfony format)
                if (storedHash.startsWith("$2y$")) {
                    storedHash = "$2a$" + storedHash.substring(4);
                }

                // Verify password
                if (BCrypt.checkpw(password, storedHash)) {
                    // Handle "Remember Me" functionality
                    if (rememberMeCheckBox.isSelected()) {
                        saveRememberedUser(email);
                    } else {
                        clearSavedLogin();
                    }

                    // Reset failed attempts on successful login
                    resetFailedAttempts(email);

                    // Check if formateur is not yet accepted
                    if ("formateur".equalsIgnoreCase(role) && status == 0) {
                        statusLabel.setText("Votre demande est en cours de traitement. Veuillez Patientez !");
                        return;
                    }

                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(role);
                    user.setPhoto_profil(rs.getString("photo_profil"));

                    UserSession.getInstance().setCurrentUser(user);

                    redirectBasedOnRole(role);
                } else {
                    // Invalid password - increment failed attempts
                    incrementFailedAttempts(email);

                    try {
                        if (isAccountLocked(email)) {
                            LocalDateTime unlockTime = getAccountLockoutTime(email);
                            statusLabel.setText("Compte bloqué temporairement. Réessayez après " +
                                    unlockTime.format(formatter));
                        } else {
                            int attemptsLeft = MAX_FAILED_ATTEMPTS - getFailedAttempts(email);
                            statusLabel.setText("Mot de passe incorrect. Tentatives restantes: " + attemptsLeft);
                            highlightField(passwordField, true);
                        }
                    } catch (SQLException e) {
                        statusLabel.setText("Erreur lors de la vérification des tentatives");
                        e.printStackTrace();
                    }
                }
            } else {
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

    private int getFailedAttempts(String email) throws SQLException {
        PreparedStatement pst = cnx.prepareStatement(
                "SELECT failed_attempts FROM account_security WHERE email = ?");
        pst.setString(1, email);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return rs.getInt("failed_attempts");
        }

        return 0;
    }

    private LocalDateTime getAccountLockoutTime(String email) throws SQLException {
        PreparedStatement pst = cnx.prepareStatement(
                "SELECT lockout_time FROM account_security WHERE email = ?");
        pst.setString(1, email);
        ResultSet rs = pst.executeQuery();

        if (rs.next() && rs.getTimestamp("lockout_time") != null) {
            return rs.getTimestamp("lockout_time").toLocalDateTime();
        }

        return null;
    }

    private void incrementFailedAttempts(String email) {
        try {
            // First check if the entry exists
            PreparedStatement checkStmt = cnx.prepareStatement(
                    "SELECT email FROM account_security WHERE email = ?");
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Update existing record
                PreparedStatement updateStmt = cnx.prepareStatement(
                        "UPDATE account_security SET failed_attempts = failed_attempts + 1 WHERE email = ?");
                updateStmt.setString(1, email);
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                // Insert new record
                PreparedStatement insertStmt = cnx.prepareStatement(
                        "INSERT INTO account_security (email, failed_attempts) VALUES (?, 1)");
                insertStmt.setString(1, email);
                insertStmt.executeUpdate();
                insertStmt.close();
            }
            checkStmt.close();

            // Check if we need to lock the account
            int attempts = getFailedAttempts(email);
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                lockAccount(email);
            }

        } catch (SQLException e) {
            System.err.println("Error incrementing failed attempts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void lockAccount(String email) {
        try {
            // Set lockout time to current time + lockout duration
            LocalDateTime unlockTime = LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES);
            Timestamp unlockTimestamp = Timestamp.valueOf(unlockTime);

            PreparedStatement pst = cnx.prepareStatement(
                    "UPDATE account_security SET lockout_time = ? WHERE email = ?");
            pst.setTimestamp(1, unlockTimestamp);
            pst.setString(2, email);
            pst.executeUpdate();
            pst.close();

        } catch (SQLException e) {
            System.err.println("Error locking account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isAccountLocked(String email) throws SQLException {
        PreparedStatement pst = cnx.prepareStatement(
                "SELECT lockout_time FROM account_security WHERE email = ?");
        pst.setString(1, email);
        ResultSet rs = pst.executeQuery();

        if (rs.next() && rs.getTimestamp("lockout_time") != null) {
            LocalDateTime unlockTime = rs.getTimestamp("lockout_time").toLocalDateTime();

            if (LocalDateTime.now().isAfter(unlockTime)) {
                // Lockout period has expired
                resetFailedAttempts(email);
                return false;
            }
            return true;
        }

        return false;
    }

    private void resetFailedAttempts(String email) {
        try {
            PreparedStatement pst = cnx.prepareStatement(
                    "UPDATE account_security SET failed_attempts = 0, lockout_time = NULL WHERE email = ?");
            pst.setString(1, email);
            pst.executeUpdate();
            pst.close();
        } catch (SQLException e) {
            System.err.println("Error resetting failed attempts: " + e.getMessage());
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
                fxmlPath = "/User/student.fxml";
                break;
            default:
                showAlert(Alert.AlertType.ERROR, "Role Error", "Unknown role: " + role);
                return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 700);

        Stage stage = (Stage) emailField.getScene().getWindow();

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

        if (password.isEmpty()) {
            passwordErrorLabel.setText("Le mot de passe est requis");
            highlightField(passwordField, true);
            isValid = false;
        } else {
            highlightField(passwordField, false);
        }

        return isValid;
    }
    private boolean validateInputPassword() {
        boolean isValid = true;

        String email = emailField.getText().trim();
        String password = passwordField.getText();

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
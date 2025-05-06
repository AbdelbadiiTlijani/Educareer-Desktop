package tn.esprit.educareer.controllers.User;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tn.esprit.educareer.models.User;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import tn.esprit.educareer.services.ServiceUser;
import tn.esprit.educareer.services.SmsService;

import java.io.IOException;

public class UserRegistrationController implements Initializable {

    @FXML
    private Button PhotoButton;

    @FXML
    private TextField PhotoField;

    @FXML
    private DatePicker birthDatePicker;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;


    @FXML
    private Button backButton;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML private Label nameErrorLabel;
    @FXML private Label firstNameErrorLabel;
    @FXML private Label emailErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label roleErrorLabel;
    @FXML private Label birthDateErrorLabel;
    @FXML private Label globalErrorLabel;
    @FXML private Label photoErrorLabel;

    // Captcha components
    @FXML private Canvas captchaCanvas;
    @FXML private Button refreshCaptchaButton;
    @FXML private TextField captchaField;
    @FXML private Label captchaErrorLabel;

    // Style CSS pour les champs d'erreur
    private String errorStyle = "-fx-border-color: red; -fx-border-width: 2px;";
    private String originalStyle = "";
    private ServiceUser serviceUser = new ServiceUser();
    private String selectedPhotoPath = "";

    // Variables for captcha
    private String captchaText;
    private Random random = new Random();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roleComboBox.getItems().addAll("formateur", "student");
        registerButton.setOnAction(event -> registerUser());

        backButton.setOnAction(event -> goBack());
        PhotoButton.setOnAction(event -> selectPhoto());

        // Initialize captcha
        refreshCaptchaButton.setOnAction(event -> generateCaptcha());
        generateCaptcha();

    }
    private void generateCaptcha() {
        // Generate random captcha text (6 characters long)
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        captchaText = sb.toString();

        // Draw captcha on canvas
        GraphicsContext gc = captchaCanvas.getGraphicsContext2D();

        // Clear the canvas
        gc.setFill(Color.rgb(30, 41, 59)); // Background color
        gc.fillRect(0, 0, captchaCanvas.getWidth(), captchaCanvas.getHeight());

        // Draw the captcha text with noise
        gc.setStroke(Color.WHITE);

        // Add some random lines for noise
        for (int i = 0; i < 10; i++) {
            gc.setStroke(getRandomLightColor());
            gc.setLineWidth(0.5 + random.nextDouble());
            gc.strokeLine(
                    random.nextDouble() * captchaCanvas.getWidth(),
                    random.nextDouble() * captchaCanvas.getHeight(),
                    random.nextDouble() * captchaCanvas.getWidth(),
                    random.nextDouble() * captchaCanvas.getHeight()
            );
        }

        // Draw text
        double x = 20;
        for (char c : captchaText.toCharArray()) {
            gc.setFill(getRandomLightColor());
            gc.setFont(javafx.scene.text.Font.font("Verdana",
                    random.nextBoolean() ? javafx.scene.text.FontWeight.BOLD : javafx.scene.text.FontWeight.NORMAL,
                    18 + random.nextInt(8)));

            // Random rotation for each character
            double angle = -15 + random.nextInt(30);
            gc.save();
            gc.translate(x, 25 + random.nextInt(10));
            gc.rotate(angle);
            gc.fillText(String.valueOf(c), 0, 0);
            gc.restore();

            x += 15 + random.nextInt(5);
        }

        // Add some dots for noise
        for (int i = 0; i < 50; i++) {
            gc.setFill(getRandomLightColor());
            gc.fillOval(
                    random.nextDouble() * captchaCanvas.getWidth(),
                    random.nextDouble() * captchaCanvas.getHeight(),
                    2, 2);
        }

        // Reset captcha field
        captchaField.setText("");
        captchaErrorLabel.setText("");
        captchaField.setStyle(originalStyle);
    }

    /**
     * Generates a random light color for captcha text
     */
    private Color getRandomLightColor() {
        return Color.rgb(
                150 + random.nextInt(105),
                150 + random.nextInt(105),
                150 + random.nextInt(105)
        );
    }

    private void selectPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une photo de profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(PhotoButton.getScene().getWindow());
        if (selectedFile != null) {
            selectedPhotoPath = selectedFile.getAbsolutePath();
            PhotoField.setText(selectedFile.getName());
        }
    }
    private void registerUser() {
        try {
            resetErrorMessages();

            boolean isValid = true;

            if (nameField.getText().isEmpty()) {
                nameErrorLabel.setText("Le nom est obligatoire");
                nameField.setStyle(errorStyle);
                isValid = false;
            } else if (nameField.getText().length() < 2) {
                nameErrorLabel.setText("Le nom doit contenir au moins 2 caractères");
                nameField.setStyle(errorStyle);
                isValid = false;
            }

            if (firstNameField.getText().isEmpty()) {
                firstNameErrorLabel.setText("Le prénom est obligatoire");
                firstNameField.setStyle(errorStyle);
                isValid = false;
            } else if (firstNameField.getText().length() < 2) {
                firstNameErrorLabel.setText("Le prénom doit contenir au moins 2 caractères");
                firstNameField.setStyle(errorStyle);
                isValid = false;
            }
            if (selectedPhotoPath.isEmpty()) {

                photoErrorLabel.setText("Une photo de profil est obligatoire");
                PhotoField.setStyle(errorStyle);
                isValid = false;
            }

            if (emailField.getText().isEmpty()) {
                emailErrorLabel.setText("L'email est obligatoire");
                emailField.setStyle(errorStyle);
                isValid = false;
            } else if (!isValidEmail(emailField.getText())) {
                emailErrorLabel.setText("Format d'email invalide");
                emailField.setStyle(errorStyle);
                isValid = false;
            }

            if (passwordField.getText().isEmpty()) {
                passwordErrorLabel.setText("Le mot de passe est obligatoire");
                passwordField.setStyle(errorStyle);
                isValid = false;
            } else if (passwordField.getText().length() < 8) {
                passwordErrorLabel.setText("Le mot de passe doit contenir au moins 8 caractères");
                passwordField.setStyle(errorStyle);
                isValid = false;
            } else if (!isPasswordStrong(passwordField.getText())) {
                passwordErrorLabel.setText("Le mot de passe doit contenir majuscule, minuscule et chiffre");
                passwordField.setStyle(errorStyle);
                isValid = false;
            }

            if (roleComboBox.getValue() == null) {
                roleErrorLabel.setText("Veuillez sélectionner un rôle");
                roleComboBox.setStyle(errorStyle);
                isValid = false;
            }


            if (birthDatePicker.getValue() == null) {
                birthDateErrorLabel.setText("La date de naissance est obligatoire");
                birthDatePicker.setStyle(errorStyle);
                isValid = false;
            } else {
                LocalDate birthDate = birthDatePicker.getValue();
                LocalDate now = LocalDate.now();

                if (birthDate.isAfter(now)) {
                    birthDateErrorLabel.setText("La date ne peut pas être dans le futur");
                    birthDatePicker.setStyle(errorStyle);
                    isValid = false;
                }
            }

            // Validate CAPTCHA
            if (captchaField.getText().isEmpty()) {
                captchaErrorLabel.setText("Veuillez entrer le code CAPTCHA");
                captchaErrorLabel.setVisible(true);
                captchaField.setStyle(errorStyle);
                isValid = false;
            } else if (!captchaField.getText().equalsIgnoreCase(captchaText)) {
                captchaErrorLabel.setText("Le code CAPTCHA ne correspond pas");
                captchaErrorLabel.setVisible(true);
                captchaField.setStyle(errorStyle);
                generateCaptcha(); // Generate a new captcha
                isValid = false;
            } else {
                captchaErrorLabel.setVisible(false);
            }

            if (!isValid) {
                globalErrorLabel.setText("Veuillez corriger les erreurs dans le formulaire");
                globalErrorLabel.setVisible(true);
                return;
            }


            User user = new User();

            user.setStatus(1);

            user.setNom(nameField.getText());
            user.setPrenom(firstNameField.getText());
            user.setEmail(emailField.getText());
            user.setMdp(passwordField.getText());

            if (!selectedPhotoPath.isEmpty()) {
                try {
                    String savedPhotoPath = savePhotoToDirectory(selectedPhotoPath);
                    user.setPhoto_profil(savedPhotoPath);
                } catch (IOException e) {
                    globalErrorLabel.setText("Erreur lors de l'enregistrement de la photo: " + e.getMessage());
                    globalErrorLabel.setVisible(true);
                    return;
                }
            }
            String selectedRole = roleComboBox.getValue();
            user.setRole(selectedRole);

            if ("formateur".equalsIgnoreCase(selectedRole)) {
                user.setStatus(0);
            } else {
                user.setStatus(1);
            }

            String verificationToken = generateVerificationToken();
            user.setVerification_token(verificationToken);

            java.util.Date now = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(now.getTime());
            user.setdate_inscription(sqlDate.toString());

            LocalDate birthDate = birthDatePicker.getValue();
            java.sql.Date sqlBirthDate = java.sql.Date.valueOf(birthDate);
            user.setDate(sqlBirthDate.toString());

            serviceUser.ajouter(user);
            if (selectedRole.equals("formateur")){
                globalErrorLabel.setTextFill(javafx.scene.paint.Color.ORANGE);
                globalErrorLabel.setText("Votre demande a été prise en compte. Un administrateur validera votre compte.");
                globalErrorLabel.setVisible(true);

                // After successful registration
                String formateurFullName = user.getPrenom() + " " + user.getNom();
                SmsService.notifyAdminFormateurRegistration(formateurFullName);
            }else {
                globalErrorLabel.setTextFill(javafx.scene.paint.Color.GREEN);
                globalErrorLabel.setText("Inscription réussie ! Redirection...");
                globalErrorLabel.setVisible(true);}/////////

            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> goToLoginPage());
            pause.play();

        } catch (Exception e) {
            globalErrorLabel.setText("Erreur: " + e.getMessage());
            globalErrorLabel.setVisible(true);
            e.printStackTrace();
        }
    }
    private String savePhotoToDirectory(String sourcePath) throws IOException {
        // JavaFX storage directory
        File javafxDir = new File("src/main/resources/photos");
        if (!javafxDir.exists()) {
            javafxDir.mkdirs();
        }

        // Symfony storage directory
        File symfonyDir = new File("C:\\Users\\Mega-PC\\Desktop\\Integration-Pi (2)\\Integration-Pi\\public\\uploads\\profiles");
        if (!symfonyDir.exists()) {
            symfonyDir.mkdirs();
        }

        // Generate unique filename
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileName = timestamp + "_" + new File(sourcePath).getName();

        // File paths
        File javafxDest = new File(javafxDir, fileName);
        File symfonyDest = new File(symfonyDir, fileName);

        // Copy to JavaFX resources
        Path source = new File(sourcePath).toPath();
        Files.copy(source, javafxDest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        // Duplicate to Symfony
        Files.copy(source, symfonyDest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        // Return filename (to store in DB)
        return fileName;
    }

    // Méthode pour valider le format de l'email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    // Méthode pour vérifier la force du mot de passe
    private boolean isPasswordStrong(String password) {
        // Vérifie si le mot de passe contient au moins une majuscule, une minuscule et un chiffre
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");

        return hasUppercase && hasLowercase && hasDigit;
    }
    private void resetErrorMessages() {
        // Réinitialiser les labels d'erreur
        nameErrorLabel.setText("");
        firstNameErrorLabel.setText("");
        emailErrorLabel.setText("");
        passwordErrorLabel.setText("");
        roleErrorLabel.setText("");
        photoErrorLabel.setText("");
        birthDateErrorLabel.setText("");
        captchaErrorLabel.setText("");
        globalErrorLabel.setText("");
        globalErrorLabel.setVisible(false);
        globalErrorLabel.setTextFill(javafx.scene.paint.Color.RED);

        // Réinitialiser les styles
        nameField.setStyle(originalStyle);
        PhotoField.setStyle(originalStyle);
        firstNameField.setStyle(originalStyle);
        emailField.setStyle(originalStyle);
        passwordField.setStyle(originalStyle);
        roleComboBox.setStyle(originalStyle);
        birthDatePicker.setStyle(originalStyle);
        captchaField.setStyle(originalStyle);
    }

    private String generateVerificationToken() {
        // Génère un token aléatoire équivalent à bin2hex(random_bytes(32))
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        // Convertir en format hexadécimal (similaire à bin2hex en PHP)
        StringBuilder hexString = new StringBuilder();
        for (byte b : randomBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
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

    private void goToLoginPage() {
        try {
            // Charger la page de connexion (ajustez le chemin selon votre structure)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/Login.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle
            Stage stage = (Stage) registerButton.getScene().getWindow();

            // Définir la nouvelle scène
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

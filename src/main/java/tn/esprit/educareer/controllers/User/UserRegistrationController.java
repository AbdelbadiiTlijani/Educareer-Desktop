package tn.esprit.educareer.controllers.User;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import tn.esprit.educareer.services.ServiceUser;

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

    // Style CSS pour les champs d'erreur
    private String errorStyle = "-fx-border-color: red; -fx-border-width: 2px;";
    private String originalStyle = "";
    private ServiceUser serviceUser = new ServiceUser();
    private String selectedPhotoPath = "";

    // Vos autres éléments FXML avec @FXML

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialiser les options du ComboBox
        roleComboBox.getItems().addAll("Formateur", "Étudiant");
        // Configurer le bouton d'inscription
        registerButton.setOnAction(event -> registerUser());

        // Configurer le bouton de retour
        backButton.setOnAction(event -> goBack());
        PhotoButton.setOnAction(event -> selectPhoto());

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
            // Réinitialiser tous les messages d'erreur et styles
            resetErrorMessages();

            boolean isValid = true;

            // Validation du nom
            if (nameField.getText().isEmpty()) {
                nameErrorLabel.setText("Le nom est obligatoire");
                nameField.setStyle(errorStyle);
                isValid = false;
            } else if (nameField.getText().length() < 2) {
                nameErrorLabel.setText("Le nom doit contenir au moins 2 caractères");
                nameField.setStyle(errorStyle);
                isValid = false;
            }

            // Validation du prénom
            if (firstNameField.getText().isEmpty()) {
                firstNameErrorLabel.setText("Le prénom est obligatoire");
                firstNameField.setStyle(errorStyle);
                isValid = false;
            } else if (firstNameField.getText().length() < 2) {
                firstNameErrorLabel.setText("Le prénom doit contenir au moins 2 caractères");
                firstNameField.setStyle(errorStyle);
                isValid = false;
            }
            // Validation de la photo
            if (selectedPhotoPath.isEmpty()) {

                photoErrorLabel.setText("Une photo de profil est obligatoire");
                PhotoField.setStyle(errorStyle);
                isValid = false;
            }

            // Validation de l'email
            if (emailField.getText().isEmpty()) {
                emailErrorLabel.setText("L'email est obligatoire");
                emailField.setStyle(errorStyle);
                isValid = false;
            } else if (!isValidEmail(emailField.getText())) {
                emailErrorLabel.setText("Format d'email invalide");
                emailField.setStyle(errorStyle);
                isValid = false;
            }

            // Validation du mot de passe
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

            // Validation du rôle
            if (roleComboBox.getValue() == null) {
                roleErrorLabel.setText("Veuillez sélectionner un rôle");
                roleComboBox.setStyle(errorStyle);
                isValid = false;
            }


            // Validation de la date de naissance
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

            // Si des erreurs sont présentes, afficher un message global
            if (!isValid) {
                globalErrorLabel.setText("Veuillez corriger les erreurs dans le formulaire");
                globalErrorLabel.setVisible(true);
                return;
            }


            // Sinon, continuer avec l'inscription
            User user = new User();

            // Définir le statut à 1 comme demandé
            user.setStatus(1);

            // Définir les informations de base
            user.setNom(nameField.getText());
            user.setPrenom(firstNameField.getText());
            user.setEmail(emailField.getText());
            user.setMdp(passwordField.getText());

// Handle profile photo
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
            // Définir le rôle en fonction de la sélection
            user.setRole(roleComboBox.getValue());

            // Générer un token de vérification
            String verificationToken = generateVerificationToken();
            user.setVerification_token(verificationToken);

            // Définir la date d'inscription à maintenant
            java.util.Date now = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(now.getTime());
            user.setdate_inscription(sqlDate.toString());

            // Définir la date de naissance
            LocalDate birthDate = birthDatePicker.getValue();
            java.sql.Date sqlBirthDate = java.sql.Date.valueOf(birthDate);
            user.setDate(sqlBirthDate.toString());

            // Ajouter l'utilisateur à la base de données
            serviceUser.ajouter(user);

            // Afficher une alerte de succès
            globalErrorLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            globalErrorLabel.setText("Inscription réussie ! Redirection...");
            globalErrorLabel.setVisible(true);

            // Attendre un peu avant de rediriger
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> goToLoginPage());
            pause.play();

        } catch (Exception e) {
            globalErrorLabel.setText("Erreur: " + e.getMessage());
            globalErrorLabel.setVisible(true);
            e.printStackTrace();
        }
    }
    private String savePhotoToDirectory(String sourcePath) throws IOException {
        // Create directory if it doesn't exist
        File photosDir = new File("src/main/resources/photos");
        if (!photosDir.exists()) {
            photosDir.mkdirs();
        }

        // Generate unique filename based on timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileName = timestamp + "_" + new File(sourcePath).getName();
        File destinationFile = new File(photosDir, fileName);

        // Copy the file
        java.nio.file.Files.copy(
                new File(sourcePath).toPath(),
                destinationFile.toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
        );

        // Return the relative path to be stored in database
        return  fileName;
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

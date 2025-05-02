package tn.esprit.educareer.controllers.User;


import tn.esprit.educareer.controllers.User.FormateurDashboarddController;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ServiceUser;
import tn.esprit.educareer.utils.UserSession;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class EditProfileController {

    @FXML
    private Label PhotoFieldLabel;

    @FXML
    private Button backButton;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private VBox postsContainer;

    @FXML
    private TextField prenomField;

    @FXML
    private Label errorName;

    @FXML
    private Label errorPassword;

    @FXML
    private Label errorPrenom;

    @FXML
    private Button uploadPhotoButton;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private ServiceUser serviceUser = new ServiceUser();
    private User currentUser;
    private File selectedPhotoFile = null;

    @FXML
    public void initialize() {
        currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            nameField.setText(currentUser.getNom());
            prenomField.setText(currentUser.getPrenom());
            passwordField.setText(currentUser.getMdp());
            PhotoFieldLabel.setText(currentUser.getPhoto_profil());
        }
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            errorName.setText("");
            errorName.setVisible(false);
            highlightField(nameField, false);
        });

        prenomField.textProperty().addListener((observable, oldValue, newValue) -> {
            errorPrenom.setText("");
            errorPrenom.setVisible(false);
            highlightField(prenomField, false);
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            errorPassword.setText("");
            errorPassword.setVisible(false);
            highlightField(passwordField, false);
        });
    }

    @FXML
    void handlePhotoUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");
        
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
            "Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        
        selectedPhotoFile = fileChooser.showOpenDialog(stage);
        
        if (selectedPhotoFile != null) {
            PhotoFieldLabel.setText("Photo selected: " + selectedPhotoFile.getName());
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        clearErrors();

        boolean isValid = validateInput();

        if (!isValid) {
            return;
        }
        if (currentUser != null) {
            try {
                currentUser.setNom(nameField.getText());
                currentUser.setPrenom(prenomField.getText());
                currentUser.setMdp(passwordField.getText());

                if (selectedPhotoFile != null) {
                    // Generate unique filename
                    String originalFilename = selectedPhotoFile.getName();
                    String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    String uniqueFilename = UUID.randomUUID().toString() + extension;

                    // Define target directory and path
                    Path targetDirectory = Paths.get("src", "main", "resources", "photos");
                    Path targetPath = targetDirectory.resolve(uniqueFilename);

                    if (!Files.exists(targetDirectory)) {
                        Files.createDirectories(targetDirectory);
                    }

                    // Copy the file
                    Files.copy(selectedPhotoFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                    // Update user's photo profile - store just the filename
                    currentUser.setPhoto_profil(uniqueFilename);
                }

                serviceUser.modifier(currentUser);
                
                UserSession.getInstance().setCurrentUser(currentUser);

                selectedPhotoFile = null;
                
                PhotoFieldLabel.setText("Profile updated successfully!");
                

                
            } catch (IOException e) {
                PhotoFieldLabel.setText("Error updating profile: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleBackButton(ActionEvent event) throws IOException {

        currentUser = UserSession.getInstance().getCurrentUser();

        if (currentUser.getRole().equals("admin")) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/AdminDashboard.fxml"));
            root = loader.load();

            AdminDashboardController dashboardController = loader.getController();
            dashboardController.setupUserProfile(); // Refresh the user's profile

        } else if (currentUser.getRole().equals("formateur")) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/FormateurDashboard.fxml"));
            root = loader.load();

            FormateurDashboarddController dashboardController = loader.getController();
            dashboardController.setupUserProfile(); // You can customize or remove if not needed

        } else if (currentUser.getRole().equals("student")) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/student.fxml"));
            root = loader.load();

            studentController dashboardController = loader.getController();
            dashboardController.setupUserProfile();

        } else {
            System.out.println("Rôle inconnu : " + currentUser.getRole());
            return;
        }

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    private void highlightField(TextField field, boolean isError) {
        if (isError) {
            field.setStyle("-fx-background-color: #ffeeee;" + // test avec rose clair
                    "-fx-border-color: red;" +
                    "-fx-border-width: 2;" +
                    "-fx-text-fill: red;");
        } else {
            field.setStyle("-fx-background-color: white;" +
                    "-fx-border-color: lightgray;" +
                    "-fx-border-width: 1;" +
                    "-fx-text-fill: black;");
        }

    }
    private void clearErrors() {
        errorName.setText("");
        errorName.setVisible(false);

        errorPrenom.setText("");
        errorPrenom.setVisible(false);

        errorPassword.setText("");
        errorPassword.setVisible(false);

        highlightField(prenomField, false);
        highlightField(nameField, false);
        highlightField(passwordField, false);
    }
    private boolean validateInput() {
        boolean isValid = true;

        String name = nameField.getText().trim();
        String prenom = prenomField.getText().trim();
        String password = passwordField.getText();

        if (name.isEmpty()) {
            errorName.setText("Le nom est requis");
            errorName.setVisible(true); // Make visible
            highlightField(nameField, true);
            isValid = false;
        } else if (name.length() < 2) {
            errorName.setText("Le nom doit contenir au moins 2 caractères");
            errorName.setVisible(true); // Make visible
            highlightField(nameField, true);
            isValid = false;
        } else if (!name.matches("^[A-Za-zÀ-ÖØ-öø-ÿ\\s'-]+$")) {
            errorName.setText("Le nom ne doit contenir que des lettres");
            errorName.setVisible(true); // Make visible
            highlightField(nameField, true);
            isValid = false;
        } else {
            errorName.setText("");
            errorName.setVisible(false);
            highlightField(nameField, false);
        }

        if (prenom.isEmpty()) {
            errorPrenom.setText("Le prenom est requis");
            errorPrenom.setVisible(true); // Make visible
            highlightField(prenomField, true);
            isValid = false;
        } else if (prenom.length() < 2) {
            errorPrenom.setText("Le prenom doit contenir au moins 2 caractères");
            errorPrenom.setVisible(true); // Make visible
            highlightField(prenomField, true);
            isValid = false;
        } else if (!prenom.matches("^[A-Za-zÀ-ÖØ-öø-ÿ\\s'-]+$")) {
            errorPrenom.setText("Le prenom ne doit contenir que des lettres");
            errorPrenom.setVisible(true); // Make visible
            highlightField(prenomField, true);
            isValid = false;
        } else {
            errorPrenom.setText("");
            errorPrenom.setVisible(false);
            highlightField(prenomField, false);
        }

        if (password == null || password.isEmpty()) {
            errorPassword.setText("Le mot de passe est requis");
            errorPassword.setVisible(true);
            highlightField(passwordField, true);
            isValid = false;
        } else if (password.length() < 8) {
            errorPassword.setText("Le mot de passe doit contenir au moins 8 caractères");
            errorPassword.setVisible(true);
            highlightField(passwordField, true);
            isValid = false;
        } else if (!password.matches(".*[A-Z].*")) {
            errorPassword.setText("Le mot de passe doit contenir au moins une majuscule");
            errorPassword.setVisible(true);
            highlightField(passwordField, true);
            isValid = false;
        } else if (!password.matches(".*\\d.*")) { // Vérifier si le mot de passe contient un chiffre
            errorPassword.setText("Le mot de passe doit contenir au moins un chiffre");
            errorPassword.setVisible(true);
            highlightField(passwordField, true);
            isValid = false;
        } else {
            errorPassword.setText("");
            errorPassword.setVisible(false);
            highlightField(passwordField, false);
        }


        return isValid;
    }



}

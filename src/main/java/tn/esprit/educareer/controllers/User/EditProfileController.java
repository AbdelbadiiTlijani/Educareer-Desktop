package tn.esprit.educareer.controllers.User;

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
    private Button uploadPhotoButton;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private ServiceUser serviceUser = new ServiceUser();
    private User currentUser;
    private File selectedPhotoFile = null;

    @FXML
    public void initialize() {
        // Get current user from session
        currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Set default values in fields
            nameField.setText(currentUser.getNom());
            prenomField.setText(currentUser.getPrenom());
            passwordField.setText(currentUser.getMdp());
            PhotoFieldLabel.setText(currentUser.getPhoto_profil());
        }
    }

    @FXML
    void handlePhotoUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");
        
        // Set extension filters
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
            "Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        
        // Show open file dialog
        selectedPhotoFile = fileChooser.showOpenDialog(stage);
        
        if (selectedPhotoFile != null) {
            PhotoFieldLabel.setText("Photo selected: " + selectedPhotoFile.getName());
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (currentUser != null) {
            try {
                // Update basic info
                currentUser.setNom(nameField.getText());
                currentUser.setPrenom(prenomField.getText());
                currentUser.setMdp(passwordField.getText());

                // Handle photo upload if a new photo was selected
                if (selectedPhotoFile != null) {
                    // Generate unique filename
                    String originalFilename = selectedPhotoFile.getName();
                    String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    String uniqueFilename = UUID.randomUUID().toString() + extension;

                    // Define target directory and path
                    Path targetDirectory = Paths.get("src", "main", "resources", "photos");
                    Path targetPath = targetDirectory.resolve(uniqueFilename);

                    // Ensure directory exists
                    if (!Files.exists(targetDirectory)) {
                        Files.createDirectories(targetDirectory);
                    }

                    // Copy the file
                    Files.copy(selectedPhotoFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                    // Update user's photo profile - store just the filename
                    currentUser.setPhoto_profil(uniqueFilename);
                }

                // Update user in database
                serviceUser.modifier(currentUser);
                
                // Update session
                UserSession.getInstance().setCurrentUser(currentUser);

                // Reset selected file
                selectedPhotoFile = null;
                
                // Show success message
                PhotoFieldLabel.setText("Profile updated successfully!");
                

                
            } catch (IOException e) {
                PhotoFieldLabel.setText("Error updating profile: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleBackButton(ActionEvent event) throws IOException {
        navigateToPage(event, "/User/AdminDashboard.fxml");
    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        root = FXMLLoader.load(getClass().getResource(path));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}

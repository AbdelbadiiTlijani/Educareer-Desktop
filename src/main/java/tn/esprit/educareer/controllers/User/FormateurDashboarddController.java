package tn.esprit.educareer.controllers.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ServiceUser;
import tn.esprit.educareer.utils.UserSession;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FormateurDashboarddController {

    @FXML
    private Label companyGrowthLabel;

    @FXML
    private AnchorPane dashboardPane;

    @FXML
    private Button logoutButton;

    @FXML
    private Label reclamationStatusLabel;

    @FXML
    private Label totalCompaniesLabel;

    @FXML
    private Label totalReclamationsLabel;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label userGrowthLabel;

    @FXML
    private Label userName;

    @FXML
    private ImageView userPhoto;

    @FXML
    private Label userRole;

    @FXML
    private Button viewCompanyEmployeeButton;

    @FXML
    private Button viewUserButton;
    @FXML
    private Button editProfileButton;

    @FXML
    void handleViewUser(ActionEvent event) {

    }

    @FXML
    void handleLogout(ActionEvent event) {
// Clear the user session
        UserSession.getInstance().clearSession();

        try {
            // Navigate back to login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private tn.esprit.educareer.services.ServiceUser ServiceUser = new ServiceUser();
    public void initialize() {
        System.out.println("Admin Dashboard initialized");

        // Add hover effects for menu buttons
        setupButtonHoverEffects();


        // Set up user profile
        setupUserProfile();
    }
    public void setupUserProfile() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Set user name
            userName.setText(currentUser.getNom() + " " + currentUser.getPrenom());
            userRole.setText(currentUser.getRole());
    
            // Set user photo if available
            if (currentUser.getPhoto_profil() != null && !currentUser.getPhoto_profil().isEmpty()) {
                try {
                    // First try loading from file system
                    String projectDir = System.getProperty("user.dir");
                    Path imagePath = Paths.get(projectDir, "src", "main", "resources", "photos",
                            currentUser.getPhoto_profil());
    
                    if (Files.exists(imagePath)) {
                        Image image = new Image(imagePath.toUri().toString());
                        userPhoto.setImage(image);
                    } else {
                        // Try loading from resources as fallback
                        String resourcePath = "/photos/" + currentUser.getPhoto_profil();
                        InputStream resourceStream = getClass().getResourceAsStream(resourcePath);
                        if (resourceStream != null) {
                            Image image = new Image(resourceStream);
                            userPhoto.setImage(image);
                        } else {
                            loadDefaultImage();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error loading user photo: " + e.getMessage());
                    loadDefaultImage();
                }
            } else {
                loadDefaultImage();
            }
        }
    }
    private void loadDefaultImage() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/photos/default-avatar.png"));
            userPhoto.setImage(defaultImage);
        } catch (Exception e) {
            System.out.println("Error loading default image: " + e.getMessage());
        }
    }
    private void setupButtonHoverEffects() {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-font-size: 14;";
        String hoverStyle = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-font-size: 14;";

        setupButtonHover(viewCompanyEmployeeButton, defaultStyle, hoverStyle);
        setupButtonHover(viewUserButton, defaultStyle, hoverStyle);
    }
    private void setupButtonHover(Button button, String defaultStyle, String hoverStyle) {
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));
    }

    @FXML
    void handleViewCompanyEmployee(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReadProjets.fxml"));
            Scene scene = new Scene(loader.load(), 1000,700);

            // Get the stage and set the new scene

            Stage stage = (Stage) editProfileButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    void handleViewEvent(ActionEvent event) {

    }

    @FXML
    void handleViewReclamation(ActionEvent event) {

    }
    @FXML
    private void handleEditProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/edit_profile.fxml"));
            Scene scene = new Scene(loader.load(), 1000,700);

            // Get the stage and set the new scene

            Stage stage = (Stage) editProfileButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button Categorie;
    @FXML
    void handleCategorie(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReadCategoriesProjet.fxml"));
            Scene scene = new Scene(loader.load(), 1000,700);

            // Get the stage and set the new scene

            Stage stage = (Stage) editProfileButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}

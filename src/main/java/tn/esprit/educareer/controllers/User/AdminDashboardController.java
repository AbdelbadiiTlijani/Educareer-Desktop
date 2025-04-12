package tn.esprit.educareer.controllers.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import javafx.stage.Stage;

import tn.esprit.educareer.services.ServiceUser;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.utils.UserSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminDashboardController {

    @FXML
    private Button companyEmployeeDetailedViewButton;

    @FXML
    private Label companyGrowthLabel;

    @FXML
    private AnchorPane dashboardPane;

    @FXML
    private Button logoutButton;

    @FXML
    private ImageView userPhoto;

    @FXML
    private Label userName;

    @FXML
    private HBox recentActivity1;

    @FXML
    private HBox recentActivity3;

    @FXML
    private Label recentActivityText1;

    @FXML
    private Label recentActivityText3;

    @FXML
    private Label recentActivityTime1;

    @FXML
    private Label recentActivityTime3;

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
    private Button viewCompanyEmployeeButton;

    @FXML
    private Button viewEventButton;

    @FXML
    private Button viewReclamationButton;

    @FXML
    private Button viewUserButton;;
    @FXML
    private Label userRole;

    private ServiceUser ServiceUser = new ServiceUser();
    public void initialize() {
        System.out.println("Admin Dashboard initialized");

        // Add hover effects for menu buttons
        setupButtonHoverEffects();

        // Load dashboard statistics
        loadDashboardStatistics();
        
        // Set up user profile
        setupUserProfile();
    }
    private void loadDashboardStatistics() {
        // Load user statistics
        int totalUsers = ServiceUser.getAll().size();
        totalUsersLabel.setText(String.valueOf(totalUsers));

        // Calculate fictional growth rate (in a real app, you would calculate this from historical data)
        userGrowthLabel.setText("+12% this month");


    }
    private void setupButtonHoverEffects() {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-font-size: 14;";
        String hoverStyle = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-font-size: 14;";

        setupButtonHover(viewReclamationButton, defaultStyle, hoverStyle);
        setupButtonHover(viewCompanyEmployeeButton, defaultStyle, hoverStyle);
        setupButtonHover(viewUserButton, defaultStyle, hoverStyle);
        setupButtonHover(viewEventButton, defaultStyle, hoverStyle);
    }
    private void setupButtonHover(Button button, String defaultStyle, String hoverStyle) {
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));
    }
    
    private void setupUserProfile() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Set user name
            userName.setText(currentUser.getNom() + " " + currentUser.getPrenom());
            userRole.setText(currentUser.getRole());
            
            // Set user photo if available
            if (currentUser.getPhoto_profil() != null && !currentUser.getPhoto_profil().isEmpty()) {
                try {
                    // Construire le chemin de la photo depuis le dossier resources/photos
                    String photoPath = "/photos/" + currentUser.getPhoto_profil();
                    System.out.println("Loading image from: " + photoPath); // Pour le débogage
                    Image image = new Image(getClass().getResourceAsStream(photoPath));
                    userPhoto.setImage(image);
                } catch (Exception e) {
                    System.out.println("Error loading user photo: " + e.getMessage());
                    // Charger une image par défaut en cas d'erreur
                    try {
                        Image defaultImage = new Image(getClass().getResourceAsStream("/photos/default-avatar.png"));
                        userPhoto.setImage(defaultImage);
                    } catch (Exception ex) {
                        System.out.println("Error loading default image: " + ex.getMessage());
                    }
                }
            } else {
                // Charger une image par défaut si aucune photo n'est définie
                try {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/photos/default-avatar.png"));
                    userPhoto.setImage(defaultImage);
                } catch (Exception e) {
                    System.out.println("Error loading default image: " + e.getMessage());
                }
            }
        }
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

    @FXML
    void handleViewCompanyEmployee(ActionEvent event) {

    }

    @FXML
    void handleViewEvent(ActionEvent event) {

    }

    @FXML
    void handleViewReclamation(ActionEvent event) {

    }

    @FXML
    void handleViewUser(ActionEvent event) {
        try {
            // Load the User List page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/UserListPage.fxml"));
            Scene scene = new Scene(loader.load(), 1000,700);

            // Get the stage and set the new scene

            Stage stage = (Stage) viewUserButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }

    }

    @FXML
    void openCompanyEmployeeDetailedView(ActionEvent event) {

    }
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Page Load Failed");
        alert.setContentText(content);
        alert.show();
    }

}

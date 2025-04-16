


package tn.esprit.educareer.controllers.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import javafx.stage.Stage;

import tn.esprit.educareer.services.ServiceUser;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.utils.UserSession;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private MenuButton userProfileMenu;

    @FXML
    private MenuItem editProfileMenuItem;

    @FXML
    private MenuItem logoutMenuItem;

    @FXML
    private Label totalReclamationsLabel;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label userGrowthLabel;

    @FXML
    private Button viewOffre;

    @FXML
    private Button viewEventButton;

    @FXML
    private Button editProfileButton;


    @FXML
    private Button viewReclamationButton;

    @FXML
    private Button viewSeancebutton;

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
        setupButtonHover( viewOffre, defaultStyle, hoverStyle);
        setupButtonHover(viewUserButton, defaultStyle, hoverStyle);
        setupButtonHover(viewEventButton, defaultStyle, hoverStyle);
    }
    private void setupButtonHover(Button button, String defaultStyle, String hoverStyle) {
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));
    }

    @FXML
    void handleViewCoursbutton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cours/listCours.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);
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
    void handleViewSeancebutton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/seance/listSeance.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);
            Stage stage = (Stage) viewUserButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }


    public void setupUserProfile() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Set user name
            userName.setText(currentUser.getNom() + " " + currentUser.getPrenom());

            // We don't need to set userRole text anymore as it's not in the new UI
            // userRole.setText(currentUser.getRole());  // This line causes the error

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



    @FXML
    void handleLogout(ActionEvent event) {
        // Clear the user session
        UserSession.getInstance().clearSession();

        try {
            // Charger la page de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1000, 700);

            Stage stage = null;

            // Récupérer la source de l'événement
            Object source = event.getSource();

            if (source instanceof MenuItem) {
                // Si la source est un MenuItem, récupérer la fenêtre via son ContextMenu
                ContextMenu contextMenu = ((MenuItem) source).getParentPopup();
                if (contextMenu != null && contextMenu.getOwnerWindow() != null) {
                    stage = (Stage) contextMenu.getOwnerWindow();
                }
            } else if (source instanceof Node) {
                // Sinon, source classique (bouton, etc.)
                stage = (Stage) ((Node) source).getScene().getWindow();
            }

            if (stage != null) {
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
            } else {
                System.err.println("Impossible de déterminer le stage à partir de l'événement.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void handleoffre(ActionEvent event) {
        try {
            // Load the User List page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/offre/OffreList.fxml"));
            Scene scene = new Scene(loader.load(), 1000,700);

            // Get the stage and set the new scene

            Stage stage = (Stage) viewOffre.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }

    }
    @FXML
    public void handleEditProfile(ActionEvent event) {
        try {
            MenuItem menuItem = (MenuItem) event.getSource();

            // Récupérer le ContextMenu parent du MenuItem
            ContextMenu contextMenu = menuItem.getParentPopup();
            if (contextMenu != null && contextMenu.getOwnerWindow() != null) {
                // Récupérer la fenêtre (stage)
                Stage stage = (Stage) contextMenu.getOwnerWindow();

                // Charger le fichier FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/edit_profile.fxml"));
                Scene scene = new Scene(loader.load(), 1000, 700);

                // Appliquer la nouvelle scène au stage
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Chargement échoué");
            alert.setContentText("Impossible de charger la page du profil.");
            alert.showAndWait();
        }
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
    private void clearImageCache(ImageView imageView) {
        imageView.setImage(null);
        // Force garbage collection to clear cached images
        System.gc();
    }
    @FXML

    void handleevent(ActionEvent event) {
        try {
            // Load the User List page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event/EventList.fxml"));
            Scene scene = new Scene(loader.load(), 1000,700);

            // Get the stage and set the new scene

            Stage stage = (Stage) viewOffre.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }

    }
    @FXML
    private Button viewTypeEventButton;
    @FXML
    void handleTypeEvent(ActionEvent event) {
        try {
            // Load the User List page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TypeEvent/TypeEventList.fxml"));
            Scene scene = new Scene(loader.load(), 1000,700);

            // Get the stage and set the new scene

            Stage stage = (Stage) viewOffre.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }
    @FXML
    public void GestionEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event/AddEvent.fxml"));
            Parent addEventPage = loader.load(); // Important : utiliser Parent ici, pas AnchorPane

            Scene scene = new Scene(addEventPage);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace(); // Tu peux aussi afficher une alerte ici
        }
    }


}

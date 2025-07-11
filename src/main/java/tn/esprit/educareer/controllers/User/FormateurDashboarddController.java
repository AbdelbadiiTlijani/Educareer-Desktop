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
import javafx.stage.Stage;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.*;
import tn.esprit.educareer.utils.UserSession;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FormateurDashboarddController {

    //Projet
    @FXML
    private Button viewAjoutProjetButton;
    @FXML
    private Button viewAjoutTypeProjetButton;
    @FXML
    private Button ViewProjets;

    //Cours
    @FXML
    private Button viewAjoutCoursButton;
    @FXML
    private Button viewAjoutTypeCoursButton;
    @FXML
    private Button ViewCours;
    @FXML
    private Label totalCoursLabel;
    @FXML
    private Label totalCatégorieCoursLabel;
    @FXML
    private Label reclamationStatusLabel;

    @FXML
    private Label userGrowthLabel;



    //Seance
    @FXML
    private Button viewAjoutSeancesButton;
    @FXML
    private Button viewAjoutTypeSeanceButton;
    @FXML
    private Button ViewSeances;
    @FXML
    private Label totalSeanceLabel;
    @FXML
    private Label companyGrowthLabel;



    @FXML
    private Button editProfileButton;

    @FXML
    private MenuButton userProfileMenu;

    @FXML
    private MenuItem editProfileMenuItem;

    @FXML
    private MenuItem logoutMenuItem;

    @FXML
    private AnchorPane dashboardPane;

    @FXML
    private Button logoutButton;

    @FXML
    private Label categorieStatusLabel;

    @FXML
    private Label totalCompaniesLabel;

    @FXML
    private Label totalReclamationsLabel;

    @FXML
    private Label userName;

    @FXML
    private ImageView userPhoto;

    @FXML
    private Label userRole;


    @FXML
    private Button Categorie;

    ServiceUser ServiceUser = new ServiceUser();
    ServiceCours serviceCours = new ServiceCours();
    ServiceSeance serviceSeance = new ServiceSeance();
    ServiceCategorieCours serviceCategorieCours = new ServiceCategorieCours();


    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Page Load Failed");
        alert.setContentText(content);
        alert.show();
    }

    //Projet
    @FXML
    void handleAjoutProjetButton(ActionEvent event) {
        try {
            // Load the User List page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Projet/AddProjet.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            // Get the stage and set the new scene

            Stage stage = (Stage) viewAjoutProjetButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    @FXML
    void handleAjoutTypeProjetButton(ActionEvent event) {
        try {
            // Load the User List page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Projet/AddCategorieProjet.fxml"));
            Scene scene = new Scene(loader.load());

            // Get the stage and set the new scene

            Stage stage = (Stage) viewAjoutTypeProjetButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    @FXML
    void handleViewProjets(ActionEvent event) {
        try {
            // Load the User List page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Projet/ReadProjets.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            // Get the stage and set the new scene

            Stage stage = (Stage) ViewProjets.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    //Cours
    @FXML
    void handleAjoutCoursButton(ActionEvent event) {
        try {
            // Load the User List page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cours/ajouterCours.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            // Get the stage and set the new scene

            Stage stage = (Stage) viewAjoutCoursButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    @FXML
    void handleAjoutTypeCoursButton(ActionEvent event) {
        try {
            // Load the User List page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/categorieCours/ajouterCategorieCours.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            // Get the stage and set the new scene

            Stage stage = (Stage) viewAjoutTypeCoursButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    @FXML
    void handleViewCours(ActionEvent event) {
        try {
            // Load the User List page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cours/listCours.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            // Get the stage and set the new scene

            Stage stage = (Stage) ViewProjets.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    //Seances
    @FXML
    void handleAjoutSeancesButton(ActionEvent event) {
        try {
            // Load the User List page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/seance/ajouterSeance.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            // Get the stage and set the new scene

            Stage stage = (Stage) viewAjoutSeancesButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    @FXML
    void handleAjoutTypeSeanceButton(ActionEvent event) {
        try {
            // Load the User List page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/seance/ajouterSeance.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            // Get the stage and set the new scene

            Stage stage = (Stage) viewAjoutTypeSeanceButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    @FXML
    void handleViewSeances(ActionEvent event) {
        try {
            // Load the User List page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/seance/listSeance.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            // Get the stage and set the new scene

            Stage stage = (Stage) ViewSeances.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    @FXML
    void handleViewUser(ActionEvent event) {

    }

    @FXML
    void handleLogout(ActionEvent event) {
        // Clear the user session
        UserSession.getInstance().clearSession();
        clearSavedLogin();

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
    private void clearSavedLogin() {
        File file = new File("rememberMe.txt");
        if (file.exists()) {
            file.delete();
        }
    }


    public void initialize() {
        System.out.println("Admin Dashboard initialized");

        // Add hover effects for menu buttons
        setupButtonHoverEffects();

        // Load dashboard statistics
        loadDashboardStatistics();

        // Set up user profile
        setupUserProfile();
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
                    Path imagePath = Paths.get(projectDir, "src", "main", "resources", "photos", currentUser.getPhoto_profil());

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

    }

    private void setupButtonHover(Button button, String defaultStyle, String hoverStyle) {
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));
    }


    @FXML
    void handleViewEvent(ActionEvent event) {

    }

    @FXML
    void handleViewReclamation(ActionEvent event) {

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
    void handleCategorie(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReadCategoriesProjet.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            // Get the stage and set the new scene

            Stage stage = (Stage) Categorie.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadDashboardStatistics() {
        // Load cours statistics
        int totalCours = serviceCours.getAll().size();
        totalCoursLabel.setText(String.valueOf(totalCours));
        userGrowthLabel.setText("+12% this month");

        // Load seance statistics
        int totalSeance = serviceSeance.getAll().size();
        totalSeanceLabel.setText(String.valueOf(totalSeance));
        userGrowthLabel.setText("+12% this month");

        // Load categorieCours statistics
        int totalCategorieCours= serviceCategorieCours.getAll().size();
        totalCatégorieCoursLabel.setText(String.valueOf(totalCategorieCours));
        categorieStatusLabel.setText("3 new today");
    }
}

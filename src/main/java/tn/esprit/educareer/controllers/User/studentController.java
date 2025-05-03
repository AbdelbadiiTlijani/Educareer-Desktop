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
import tn.esprit.educareer.services.ServiceUser;
import tn.esprit.educareer.utils.UserSession;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class studentController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Label companyGrowthLabel;

    @FXML
    private MenuButton userProfileMenu;

    @FXML
    private MenuItem editProfileMenuItem;

    @FXML
    private MenuItem logoutMenuItem;

    @FXML
    private AnchorPane dashboardPane;

    @FXML
    private Button editProfileButton;
    @FXML
    private Button viewJobOffersButton;

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
    public void handleEditProfile(ActionEvent event) {
        try {
            MenuItem menuItem = (MenuItem) event.getSource();

            ContextMenu contextMenu = menuItem.getParentPopup();
            if (contextMenu != null && contextMenu.getOwnerWindow() != null) {
                Stage stage = (Stage) contextMenu.getOwnerWindow();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/edit_profile.fxml"));
                Scene scene = new Scene(loader.load(), 1000, 700);

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
    private tn.esprit.educareer.services.ServiceUser ServiceUser = new ServiceUser();
    public void initialize() {
        System.out.println("Admin Dashboard initialized");

        setupButtonHoverEffects();


        setupUserProfile();
    }
    public void setupUserProfile() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            userName.setText(currentUser.getNom() + " " + currentUser.getPrenom());


            if (currentUser.getPhoto_profil() != null && !currentUser.getPhoto_profil().isEmpty()) {
                try {
                    String projectDir = System.getProperty("user.dir");
                    Path imagePath = Paths.get(projectDir, "src", "main", "resources", "photos",
                            currentUser.getPhoto_profil());

                    if (Files.exists(imagePath)) {
                        Image image = new Image(imagePath.toUri().toString());
                        userPhoto.setImage(image);
                    } else {
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
    void handleLogout(ActionEvent event) {
        UserSession.getInstance().clearSession();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1000, 700);

            Stage stage = null;

            Object source = event.getSource();

            if (source instanceof MenuItem) {
                ContextMenu contextMenu = ((MenuItem) source).getParentPopup();
                if (contextMenu != null && contextMenu.getOwnerWindow() != null) {
                    stage = (Stage) contextMenu.getOwnerWindow();
                }
            } else if (source instanceof Node) {
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

    }

    private void navigateToPage(ActionEvent event, String path) throws IOException {
        URL fxmlLocation = getClass().getResource(path);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found at: " + path);
        }
        root = FXMLLoader.load(fxmlLocation);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root , 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    public void handleViewJobOffers(ActionEvent event) throws IOException {
        navigateToPage(event, "/offre/JobOffers.fxml");
    }



}

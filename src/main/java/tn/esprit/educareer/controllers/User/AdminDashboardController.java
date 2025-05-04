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
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ServiceUser;
import tn.esprit.educareer.utils.UserSession;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    private Button viewUserButton;
    @FXML
    private Button viewUserButton1;


    @FXML
    private Label userRole;

    private ServiceUser ServiceUser = new ServiceUser();
    @FXML
    private Button viewTypeEventButton;

    //Offre
    @FXML
    private Button viewAjoutOffrebutton;
    @FXML
    private Button viewAjoutTypeOffreButton;
    @FXML
    private Button viewListOffre;


    //Réclamation
    @FXML
    private Button viewAjoutReclamationbutton;
    @FXML
    private Button viewAjoutTypeReclamationButton;
    @FXML
    private Button viewListReclamation;


    //Event
    @FXML
    private Button viewAjoutEventbutton;
    @FXML
    private Button viewAjoutTypeEventButton;
    @FXML
    private Button viewListEvent;

    @FXML
    private Button viewSeancebutton;

    @FXML
    private Button viewCoursbutton;





    @FXML
    private Button viewStatActuelButton;

    @FXML
    private Button viewStatPredictionButton;


    public void initialize() {
        System.out.println("Admin Dashboard initialized");

        setupButtonHoverEffects();

        loadDashboardStatistics();

        setupUserProfile();
    }

    private void loadDashboardStatistics() {
        int totalUsers = ServiceUser.getAll().size();
        totalUsersLabel.setText(String.valueOf(totalUsers));

        userGrowthLabel.setText("+12% this month");
    }

    private void setupButtonHoverEffects() {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-font-size: 14;";
        String hoverStyle = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-font-size: 14;";

        //Offre
        setupButtonHover(viewAjoutOffrebutton, defaultStyle, hoverStyle);
        setupButtonHover(viewAjoutTypeOffreButton, defaultStyle, hoverStyle);

        //Réclamtion
        setupButtonHover(viewAjoutReclamationbutton, defaultStyle, hoverStyle);
        setupButtonHover(viewAjoutTypeReclamationButton, defaultStyle, hoverStyle);

        //Event
        setupButtonHover(viewAjoutEventbutton, defaultStyle, hoverStyle);
        setupButtonHover(viewAjoutTypeEventButton, defaultStyle, hoverStyle);
    }

    private void setupButtonHover(Button button, String defaultStyle, String hoverStyle) {
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));
    }

    public void setupUserProfile() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            userName.setText(currentUser.getNom() + " " + currentUser.getPrenom());


            if (currentUser.getPhoto_profil() != null && !currentUser.getPhoto_profil().isEmpty()) {
                try {
                    String projectDir = System.getProperty("user.dir");
                    Path imagePath = Paths.get(projectDir, "src", "main", "resources", "photos", currentUser.getPhoto_profil());

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

    @FXML
    void handleLogout(ActionEvent event) {
        UserSession.getInstance().clearSession();
        clearSavedLogin();


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
    private void clearSavedLogin() {
        File file = new File("rememberMe.txt");
        if (file.exists()) {
            file.delete();
        }
    }



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


    //Offre
    @FXML
    public void handleAjoutOffreButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/offre/AjouterOffre.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            Stage stage = (Stage) viewAjoutOffrebutton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    @FXML
    public void handleAjoutTypeOffreButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/offre/AjouterTypeOffre.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            Stage stage = (Stage) viewAjoutTypeOffreButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    @FXML
    public void handleListOffre(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/offre/OffreList.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);


            Stage stage = (Stage) viewListOffre.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    //Réclamation
    @FXML
    public void handleAjoutReclamationButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/ReclamationList1.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);


            Stage stage = (Stage) viewAjoutReclamationbutton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    @FXML
    public void handleAjoutTypeReclamationButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/AjouterTypeReclamation.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);


            Stage stage = (Stage) viewAjoutTypeReclamationButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    @FXML
    public void handleListReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/ReclamationList.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);


            Stage stage = (Stage) viewListReclamation.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    @FXML
    public void handleListEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event/EventList.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);


            Stage stage = (Stage) viewListReclamation.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
        }
    }

    //Evenements
    @FXML
    public void handleAjoutEvenementButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event/AddEvent.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);


            Stage stage = (Stage) viewAjoutEventbutton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load User List page: " + e.getMessage());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/UserListPage.fxml"));
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
    public void handleAjoutTypeEventButton(ActionEvent event) {
        try {
            // Load the User List page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TypeEvent/AddTypeEvent.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);


            Stage stage = (Stage) viewAjoutTypeEventButton.getScene().getWindow();
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
        System.gc();
    }

    @FXML
    void handleevent(ActionEvent event) {
        try {
            // Load the User List page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event/EventList.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            // Get the stage and set the new scene

            Stage stage = (Stage) viewListEvent.getScene().getWindow();
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
            e.printStackTrace();
        }
    }
@FXML
public void handleViewStatActuel(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Projet/participation_chart.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 700);

        Stage stage = (Stage) viewStatActuelButton.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        showErrorAlert("Navigation Error", "Failed to load Participation Chart page: " + e.getMessage());
    }
}

@FXML
public void handleViewStatPrediction(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Projet/prediction_chart.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 700);

        Stage stage = (Stage) viewStatPredictionButton.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        // Appeler l'API dans un thread séparé
        new Thread(() -> {
            try {
                URL url = new URL("http://127.0.0.1:9002/prediction");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("API appelée avec succès !");
                } else {
                    System.out.println("Erreur lors de l'appel API. Code: " + responseCode);
                }
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de la connexion à l'API: " + e.getMessage());
            }
        }).start();

    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Erreur lors du chargement de prediction_chart.fxml : " + e.getMessage());
    }
}

@FXML
void handleViewPlanning(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event/CalendarView.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 700);

        Stage stage = (Stage) viewUserButton1.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        showErrorAlert("Navigation Error", "Failed to load Calendar View: " + e.getMessage());
    }
}

}

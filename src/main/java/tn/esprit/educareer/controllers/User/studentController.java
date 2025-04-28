package tn.esprit.educareer.controllers.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tn.esprit.educareer.models.User;

import tn.esprit.educareer.services.ServiceUser;
import tn.esprit.educareer.utils.UserSession;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.PauseTransition;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class studentController {

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
        System.out.println("Student Dashboard initialized");

        setupButtonHoverEffects();
        setupUserProfile();

        if (!UserSession.getInstance().isMotivationalQuoteShown()) {
            new Thread(() -> {
                String quote = getMotivationalQuote();
                javafx.application.Platform.runLater(() -> {
                    System.out.println("Displaying the quote notification...");
                    showMotivationalPopUp(quote);
                    UserSession.getInstance().setMotivationalQuoteShown(true);
                });
            }).start();
        }
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
    @FXML
    void handleViewProjects(ActionEvent event) {
        try {
            // Charger la nouvelle vue listprojetclient.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Projet/listprojetclient.fxml"));
            Parent root = loader.load();

            // Obtenez la scène actuelle et le stage
            Scene scene = new Scene(root, 1000, 700);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Mettre à jour la scène et l'afficher
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Chargement échoué");
            alert.setContentText("Impossible de charger la page des projets.");
            alert.showAndWait();
        }
    }


    private String getMotivationalQuote() {
        String apiUrl = "https://zenquotes.io/api/random";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Response from ZenQuotes: " + response.toString());

                    org.json.JSONArray array = new org.json.JSONArray(response.toString());
                    if (array.length() > 0) {
                        // Extract the quote text
                        JSONObject first = array.getJSONObject(0);
                        return first.getString("q");
                    }
                }
            } else {
                System.out.println("Error fetching quote: HTTP code " + code);
            }
        } catch (Exception e) {
            System.out.println("Error fetching motivational quote: " + e.getMessage());
        }
        return "Stay positive and keep pushing forward!";
    }
    private void showMotivationalPopUp(String quote) {
        // Create a new stage for the pop-up
        Stage popUpStage = new Stage();
        popUpStage.initStyle(StageStyle.UNDECORATED);
        popUpStage.setAlwaysOnTop(true);

        // Create a label to display the quote with modern styling
        Label quoteLabel = new Label(quote);
        quoteLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #ffffff; -fx-padding: 15 20; -fx-alignment: center-left; -fx-wrap-text: true;");
        quoteLabel.setMaxWidth(380);

        // Create a close button with modern styling
        Button closeButton = new Button("✕");
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-padding: 5; -fx-cursor: hand;");
        closeButton.setOnMouseEntered(e -> closeButton.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-padding: 5; -fx-cursor: hand;"));
        closeButton.setOnMouseExited(e -> closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-padding: 5; -fx-cursor: hand;"));
        closeButton.setOnAction(e -> popUpStage.close());

        // Create a header with title and icon
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-padding: 15 20 0 20;");

        // Add a small icon before the title
        Label iconLabel = new Label("💡");
        iconLabel.setStyle("-fx-font-size: 16px;");

        Label titleLabel = new Label("Daily Motivation");
        titleLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");

        header.getChildren().addAll(iconLabel, titleLabel);

        // Create a layout for the pop-up content
        VBox vbox = new VBox(5);
        vbox.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #3498db); -fx-background-radius: 0; -fx-padding: 0 0 15 0;");
        vbox.setAlignment(Pos.TOP_LEFT);
        vbox.setPrefWidth(400); // Set preferred width

        // Add shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        shadow.setRadius(10);
        shadow.setOffsetX(0);
        shadow.setOffsetY(2);
        vbox.setEffect(shadow);

        // Create a top bar with close button
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setStyle("-fx-background-color: rgba(0,0,0,0.1); -fx-padding: 5 10;");
        topBar.getChildren().add(closeButton);

        vbox.getChildren().addAll(topBar, header, quoteLabel);

        // Set the scene with the vbox layout
        Scene scene = new Scene(vbox);
        scene.setFill(Color.TRANSPARENT); // Make scene background transparent
        popUpStage.setScene(scene);

        // Force sizing to ensure everything fits
        popUpStage.sizeToScene();

        // This fixes the white rectangle by setting the stage background transparent
        popUpStage.initStyle(StageStyle.TRANSPARENT);

        // Position the pop-up in the bottom-right corner
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        popUpStage.setX(screenBounds.getMaxX() - 420);
        popUpStage.setY(screenBounds.getMaxY() - 200); // Fixed position at bottom right

        // Add slide-in animation from right (with initial transparent state)
        vbox.setTranslateX(100);
        vbox.setOpacity(0);
        Timeline slideIn = new Timeline(
                new KeyFrame(Duration.seconds(0.3),
                        new KeyValue(vbox.translateXProperty(), 0),
                        new KeyValue(vbox.opacityProperty(), 1)
                )
        );
        slideIn.play();

        // Show the pop-up
        popUpStage.show();

        // Auto-close after 10 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(10));
        delay.setOnFinished(event -> {
            Timeline slideOut = new Timeline(
                    new KeyFrame(Duration.seconds(0.3),
                            new KeyValue(vbox.translateXProperty(), 100),
                            new KeyValue(vbox.opacityProperty(), 0)
                    )
            );
            slideOut.setOnFinished(e -> popUpStage.close());
            slideOut.play();
        });
        delay.play();
    }
    




}

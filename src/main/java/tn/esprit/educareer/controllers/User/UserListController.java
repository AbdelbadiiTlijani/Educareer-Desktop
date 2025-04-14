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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ServiceUser;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;

public class UserListController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private ServiceUser serviceUser = new ServiceUser();

    @FXML
    private ListView<User> userListView;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> roleFilter;


    @FXML
    public void initialize() {
        userListView.getItems().addAll(
                serviceUser.getAll().stream()
                        .filter(user -> !user.getRole().equalsIgnoreCase("admin"))
                        .toList()
        );
        // Set up the cell factory for the ListView
        userListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<User>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        if (empty || user == null) {
                            setGraphic(null);
                        } else {
                            // Create a container for the user information
                            HBox container = new HBox(10);
                            container.setStyle("-fx-padding: 10px; -fx-background-color: white; -fx-background-radius: 5px;");

                            // Create and set up the profile image
                            ImageView profileImage = new ImageView();
                            profileImage.setFitHeight(50);
                            profileImage.setFitWidth(50);
                            profileImage.setPreserveRatio(true);

                            try {
                                // First try loading from file system
                                String projectDir = System.getProperty("user.dir");
                                Path imagePath = Paths.get(projectDir, "src", "main", "resources", "photos",
                                        user.getPhoto_profil());

                                if (Files.exists(imagePath)) {
                                    Image image = new Image(imagePath.toUri().toString());
                                    profileImage.setImage(image);
                                } else {
                                    // Try loading from resources as fallback
                                    String resourcePath = "/photos/" + user.getPhoto_profil();
                                    InputStream resourceStream = getClass().getResourceAsStream(resourcePath);
                                    if (resourceStream != null) {
                                        Image image = new Image(resourceStream);
                                        profileImage.setImage(image);
                                    } else {
                                        loadDefaultImage(profileImage);
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Error loading user photo: " + e.getMessage());
                                loadDefaultImage(profileImage);
                            }

                            // Create labels for user information
                            VBox userInfo = new VBox(5);
                            Label nameLabel = new Label(user.getNom() + " " + user.getPrenom());
                            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                            Label emailLabel = new Label(user.getEmail());
                            emailLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

                            Label roleLabel = new Label(user.getRole());
                            roleLabel.setStyle("-fx-text-fill: #4e54c8; -fx-font-size: 12px;");

                            userInfo.getChildren().addAll(nameLabel, emailLabel, roleLabel);

                            // Create delete button
                            Button deleteButton = new Button("Supprimer");
                            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                            deleteButton.setOnAction(event -> {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("Confirmation de suppression");
                                alert.setHeaderText("Supprimer l'utilisateur");
                                alert.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");

                                if (alert.showAndWait().get() == ButtonType.OK) {
                                    serviceUser.supprimer(user);
                                    userListView.getItems().remove(user);
                                }
                            });

                            // Add all elements to the container
                            container.getChildren().addAll(profileImage, userInfo, deleteButton);
                            setGraphic(container);
                        }
                    }
                };
            }
        });

        roleFilter.getItems().addAll("Tous", "student", "formateur");
        roleFilter.setValue("Tous"); // default value

        // Add listeners
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filterUserList();
        });

        roleFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            filterUserList();
        });


    }

    private void filterUserList() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedRole = roleFilter.getValue();

        List<User> filtered = serviceUser.getAll().stream()
                .filter(user -> !user.getRole().equalsIgnoreCase("admin")) // exclude admin
                .filter(user -> user.getNom().toLowerCase().contains(searchText) || user.getPrenom().toLowerCase().contains(searchText))
                .filter(user -> selectedRole.equals("Tous") || user.getRole().equalsIgnoreCase(selectedRole))
                .toList();

        userListView.getItems().setAll(filtered);
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

    // Add this helper method at the end of the class
    private void loadDefaultImage(ImageView imageView) {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/photos/default-avatar.png"));
            imageView.setImage(defaultImage);
        } catch (Exception ex) {
            System.out.println("Error loading default image: " + ex.getMessage());
        }
    }
}

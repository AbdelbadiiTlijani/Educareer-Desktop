package tn.esprit.educareer.controllers.User;

import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;

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
import tn.esprit.educareer.services.EmailService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class UserListController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private ServiceUser serviceUser = new ServiceUser();
    // Email service configuration - replace with your actual SMTP settings
    private EmailService emailService = new EmailService(
            "badi3tlijani12@gmail.com",     // Your Gmail address
            "dgbk saoi bviw igml"  // Your Gmail App Password
    );

    @FXML
    private ListView<User> userListView;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> roleFilter;

    @FXML
    private ComboBox<String> statusFilter;


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

                            VBox userInfo = new VBox(5);
                            Label nameLabel = new Label(user.getNom() + " " + user.getPrenom());
                            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                            Label emailLabel = new Label(user.getEmail());
                            emailLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

                            HBox roleContainer = new HBox(5); // spacing between dot and text
                            roleContainer.setAlignment(Pos.CENTER_LEFT);

                            Circle statusDot = new Circle(5); // 5px radius bullet point

                            if (user.getRole().equalsIgnoreCase("formateur")) {
                                if (user.getStatus() == 0) {
                                    statusDot.setFill(Color.RED); // Inactive
                                } else {
                                    statusDot.setFill(Color.LIMEGREEN); // Active
                                }
                            } else {
                                statusDot.setVisible(false); // hide dot if not formateur
                            }

                            Label roleLabel = new Label(user.getRole());
                            roleLabel.setStyle("-fx-text-fill: #4e54c8; -fx-font-size: 12px;");

                            // Add status label
                            Label statusLabel = new Label();
                            if (user.getRole().equalsIgnoreCase("formateur")) {
                                statusLabel.setText(user.getStatus() == 1 ? "Actif" : "Inactif");
                                statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " +
                                        (user.getStatus() == 1 ? "#27ae60" : "#e74c3c") + ";");
                            } else {
                                statusLabel.setVisible(false);
                            }

                            roleContainer.getChildren().addAll(statusDot, roleLabel, statusLabel);

                            // Add all information to userInfo - ONLY ADD ONCE
                            userInfo.getChildren().addAll(nameLabel, emailLabel, roleContainer);

                            // Create delete button
                            Button deleteButton = new Button("Supprimer");
                            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

                            // Create approveButton for formateurs with status 0
                            if (user.getRole().equalsIgnoreCase("formateur") && user.getStatus() == 0) {
                                Button approveButton = new Button("Approuver");
                                approveButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

                                approveButton.setOnAction(event -> {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Confirmation d'acceptation");
                                    alert.setHeaderText("Accepter le formateur");
                                    alert.setContentText("Êtes-vous sûr de vouloir accepter cet utilisateur ?");

                                    // Vérifie si l'utilisateur a cliqué sur OK
                                    if (alert.showAndWait().get() == ButtonType.OK) {
                                        // Show loading indicator
                                        Button clickedButton = (Button) event.getSource();
                                        clickedButton.setDisable(true);
                                        clickedButton.setText("Traitement...");

                                        // Create a background task for email sending
                                        Task<Void> emailTask = new Task<>() {
                                            @Override
                                            protected Void call() {
                                                try {
                                                    // Send email notification
                                                    sendApprovalEmail(user);
                                                    // Update user status in database
                                                    serviceUser.approuverUser(user);
                                                    return null;
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    return null;
                                                }
                                            }
                                        };

                                        // Handle task completion
                                        emailTask.setOnSucceeded(e -> {
                                            Platform.runLater(() -> {
                                                // Show success message
                                                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                                                successAlert.setTitle("Succès");
                                                successAlert.setHeaderText("Formateur approuvé");
                                                successAlert.setContentText("Le formateur a été approuvé avec succès et un email de notification a été envoyé.");
                                                successAlert.showAndWait();

                                                // Refresh the list
                                                filterUserList();
                                            });
                                        });

                                        emailTask.setOnFailed(e -> {
                                            Platform.runLater(() -> {
                                                // Show error message
                                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                                errorAlert.setTitle("Erreur");
                                                errorAlert.setHeaderText("Erreur lors de l'approbation");
                                                errorAlert.setContentText("Une erreur est survenue. Veuillez réessayer.");
                                                errorAlert.showAndWait();

                                                // Reset button
                                                clickedButton.setDisable(false);
                                                clickedButton.setText("Approuver");
                                            });
                                        });

                                        // Start the task
                                        new Thread(emailTask).start();
                                    }
                                });

                                // Create a container for the buttons
                                HBox buttonsContainer = new HBox(5);
                                buttonsContainer.getChildren().addAll(approveButton, deleteButton);

                                // Add all elements to the container
                                container.getChildren().addAll(profileImage, userInfo, buttonsContainer);
                            } else {
                                // Add all elements to the container for regular users
                                container.getChildren().addAll(profileImage, userInfo, deleteButton);
                            }

                            // Set up delete button action
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

                            setGraphic(container);
                        }
                    }
                };
            }
        });

        roleFilter.getItems().addAll("Tous", "student", "formateur");
        roleFilter.setValue("Tous"); // default value

        statusFilter.getItems().addAll("Tous", "Actif", "Inactif");
        statusFilter.setValue("Tous"); // default value

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filterUserList();
        });

        roleFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            filterUserList();
        });

        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            filterUserList();
        });
    }

    private void sendApprovalEmail(User user) {
        String recipientEmail = user.getEmail();
        String subject = "EduCareer - Votre compte formateur a été approuvé";

        // Create HTML email content
        String emailContent =
                "<html>" +
                        "<head>" +
                        "<style>" +
                        "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                        ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                        ".header { background-color: #4e54c8; color: white; padding: 20px; text-align: center; }" +
                        ".content { padding: 20px; border: 1px solid #ddd; border-top: none; }" +
                        ".footer { font-size: 12px; text-align: center; margin-top: 20px; color: #777; }" +
                        ".button { display: inline-block; background-color: #27ae60; color: white; padding: 10px 20px; " +
                        "text-decoration: none; border-radius: 5px; margin-top: 20px; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class='container'>" +
                        "<div class='header'>" +
                        "<h1>Félicitations " + user.getPrenom() + " " + user.getNom() + " !</h1>" +
                        "</div>" +
                        "<div class='content'>" +
                        "<p>Votre compte formateur sur EduCareer a été approuvé.</p>" +
                        "<p>Vous pouvez maintenant vous connecter et commencer à créer des cours, interagir avec les étudiants " +
                        "et accéder à toutes les fonctionnalités de formateur.</p>" +
                        "<p>Nous sommes ravis de vous accueillir dans notre communauté d'enseignants.</p>" +
                        "<center><a href='http://www.educareer.tn/login' class='button'>Se connecter maintenant</a></center>" +
                        "<p>Si vous avez des questions, n'hésitez pas à contacter notre équipe de support.</p>" +
                        "<p>Bien cordialement,<br>L'équipe EduCareer</p>" +
                        "</div>" +
                        "<div class='footer'>" +
                        "© " + java.time.Year.now().getValue() + " EduCareer. Tous droits réservés." +
                        "</div>" +
                        "</div>" +
                        "</body>" +
                        "</html>";

        // Send the email
        emailService.sendEmail(recipientEmail, subject, emailContent);
    }

    private void filterUserList() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedRole = roleFilter.getValue();
        String selectedStatus = statusFilter.getValue();

        List<User> filtered = serviceUser.getAll().stream()
                .filter(user -> !user.getRole().equalsIgnoreCase("admin")) // exclude admin
                .filter(user -> user.getNom().toLowerCase().contains(searchText) || user.getPrenom().toLowerCase().contains(searchText))
                .filter(user -> selectedRole.equals("Tous") || user.getRole().equalsIgnoreCase(selectedRole))
                .filter(user -> {
                    if (selectedStatus.equals("Tous")) {
                        return true;
                    } else if (selectedStatus.equals("Actif")) {
                        return user.getStatus() == 1 || !user.getRole().equalsIgnoreCase("formateur");
                    } else if (selectedStatus.equals("Inactif")) {
                        return user.getStatus() == 0 && user.getRole().equalsIgnoreCase("formateur");
                    }
                    return true;
                })
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
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }


    private void loadDefaultImage(ImageView imageView) {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/photos/default-avatar.png"));
            imageView.setImage(defaultImage);
        } catch (Exception ex) {
            System.out.println("Error loading default image: " + ex.getMessage());
        }
    }
}
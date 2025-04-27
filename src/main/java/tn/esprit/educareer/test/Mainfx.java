package tn.esprit.educareer.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.utils.UserSession;

import java.io.*;
import java.time.LocalDate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import tn.esprit.educareer.utils.MyConnection;

public class Mainfx extends Application {

    private static final String REMEMBER_ME_FILE = "rememberMe.txt";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        String rememberedUser = getRememberedUser();
        Parent root;

        if (rememberedUser != null) {
            User user = findUserByEmail(rememberedUser);

            if (user != null) {
                UserSession.getInstance().setCurrentUser(user);

                // Redirect based on user role
                String fxmlPath;
                switch (user.getRole().toLowerCase()) {
                    case "admin":
                        fxmlPath = "/User/AdminDashboard.fxml";
                        break;
                    case "formateur":
                        fxmlPath = "/User/FormateurDashboard.fxml";
                        break;
                    case "student":
                        fxmlPath = "/User/student.fxml";
                        break;
                    default:
                        fxmlPath = "/main.fxml";
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                root = loader.load();
            } else {
                // User not found or invalid, load main screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
                root = loader.load();
            }
        } else {
            // No remembered user, load main screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            root = loader.load();
        }

        // Set up the scene with a larger aspect ratio
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("EduCareer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String getRememberedUser() {
        File file = new File(REMEMBER_ME_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String[] data = reader.readLine().split(",");
                if (data.length == 2) {
                    String email = data[0];
                    LocalDate expirationDate = LocalDate.parse(data[1]);
                    if (LocalDate.now().isBefore(expirationDate)) {
                        return email;
                    } else {
                        clearSavedLogin();
                    }
                }
            } catch (IOException e) {
                System.out.println("Failed to read rememberMe file: " + e.getMessage());
            }
        }
        return null;
    }

    private void clearSavedLogin() {
        File file = new File(REMEMBER_ME_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    private User findUserByEmail(String email) {
        User user = null;
        Connection cnx = MyConnection.getInstance().getCnx();

        try {
            String query = "SELECT * FROM user WHERE email = ?";
            PreparedStatement pst = cnx.prepareStatement(query);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setPhoto_profil(rs.getString("photo_profil"));

                // Check if formateur is accepted
                int status = rs.getInt("status");
                if ("formateur".equalsIgnoreCase(user.getRole()) && status == 0) {
                    // Formateur not yet accepted
                    return null;
                }
            }

            pst.close();
        } catch (Exception e) {
            System.err.println("Error finding user by email: " + e.getMessage());
            e.printStackTrace();
        }

        return user;
    }
}
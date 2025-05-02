package tn.esprit.educareer.utils;

import javafx.stage.Stage;
import tn.esprit.educareer.models.User;

public class UserSession {
    private static UserSession instance;
    private User currentUser;
    private Stage primaryStage;

    private UserSession() {
        // Private constructor to prevent instantiation
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setCurrentUser(User user) {

        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }
    // Store reference to the primary stage
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void clearSession() {
        this.currentUser = null;
        motivationalQuoteShown = false;

    }
    private boolean motivationalQuoteShown = false;

    public boolean isMotivationalQuoteShown() {
        return motivationalQuoteShown;
    }

    public void setMotivationalQuoteShown(boolean shown) {
        this.motivationalQuoteShown = shown;
    }
} 
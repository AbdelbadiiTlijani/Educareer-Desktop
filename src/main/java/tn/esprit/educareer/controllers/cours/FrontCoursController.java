package tn.esprit.educareer.controllers.cours;

import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.educareer.models.Cours;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ServiceAvisCours;
import tn.esprit.educareer.services.ServiceCours;
import tn.esprit.educareer.utils.UserSession;

import java.io.IOException;

public class FrontCoursController {
    ServiceCours serviceCours = new ServiceCours();
    ServiceAvisCours serviceAvisCours = new ServiceAvisCours();

    @FXML
    private TilePane coursContainer;

    @FXML
    private TextField searchField;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        searchField.setPromptText("Rechercher un cours...");
        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                loadCours(newValue);
            }
        });

        coursContainer.setHgap(20);
        coursContainer.setVgap(20);
        coursContainer.setPadding(new Insets(20));
        coursContainer.setPrefColumns(3);

        loadCours("");

        backButton.setOnAction(e -> goBack());
    }

    private void loadCours(String filter) {
        coursContainer.getChildren().clear();
        for (Cours cours : serviceCours.getAllSortedByPositiveAvis()) {
            addCoursCard(cours);
        }
    }

    private void openAvisPopup(Cours cours) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Donner votre avis");

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20,0,0,5);");

        Label label = new Label("\u2728 Écrivez votre avis sur le cours :");
        TextArea avisTextArea = new TextArea();
        avisTextArea.setPromptText("Votre avis...");
        avisTextArea.setWrapText(true);
        avisTextArea.setPrefSize(300, 150);

        Button envoyerButton = new Button("Envoyer");
        envoyerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px;");

        envoyerButton.setOnAction(event -> {
            String avisText = avisTextArea.getText().trim();
            if (!avisText.isEmpty()) {
                String result = serviceAvisCours.getAvis(avisText);
                User currentUser = UserSession.getInstance().getCurrentUser();
                serviceAvisCours.ajouterAvis(cours, avisText, result, currentUser);

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Votre avis a été envoyé avec succès !");
                successAlert.showAndWait();

                avisTextArea.clear();
                popupStage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez écrire un avis avant d'envoyer !");
                alert.showAndWait();
            }
        });

        vbox.getChildren().addAll(label, avisTextArea, envoyerButton);

        Scene scene = new Scene(vbox, 400, 300);
        popupStage.setScene(scene);
        ScaleTransition st = new ScaleTransition(Duration.millis(300), vbox);
        st.setFromX(0.5);
        st.setFromY(0.5);
        st.setToX(1);
        st.setToY(1);
        st.play();
        popupStage.showAndWait();
    }

    private void addCoursCard(Cours cours) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d1d1d1; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10,0,0,5);");

        ImageView imageView = new ImageView(new Image(cours.getImage()));
        imageView.setFitWidth(250);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        Label titreLabel = new Label(cours.getNom());
        titreLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label prerequisLabel = new Label("Prérequis : " + cours.getRequirement());
        prerequisLabel.setStyle("-fx-font-size: 14px;");

        Label categorieLabel = new Label("Catégorie : " + cours.getCategorie().getNom());
        categorieLabel.setStyle("-fx-font-size: 14px;");

        Button avisButton = new Button("\uD83D\uDCDD Votre avis");
        avisButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px;");
        avisButton.setOnAction(e -> openAvisPopup(cours));

        // Animation au survol
        card.setOnMouseEntered(event -> {
            card.setScaleX(1.03);
            card.setScaleY(1.03);
            card.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #00bcd4; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20,0,0,5);");
        });

        card.setOnMouseExited(event -> {
            card.setScaleX(1);
            card.setScaleY(1);
            card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d1d1d1; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10,0,0,5);");
        });

        card.getChildren().addAll(imageView, titreLabel, prerequisLabel, categorieLabel, avisButton);

        coursContainer.getChildren().add(card);
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/student.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 1000, 700);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
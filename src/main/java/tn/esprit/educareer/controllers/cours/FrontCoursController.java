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
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.educareer.models.AvisCours;
import tn.esprit.educareer.models.Cours;
import tn.esprit.educareer.models.ReactionCours;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ReactionCoursService;
import tn.esprit.educareer.services.ServiceAvisCours;
import tn.esprit.educareer.services.ServiceCours;
import tn.esprit.educareer.utils.UserSession;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;

import java.io.IOException;

public class FrontCoursController {
    ServiceCours serviceCours = new ServiceCours();
    ServiceAvisCours serviceAvisCours = new ServiceAvisCours();
    ReactionCoursService reactionCoursService = new ReactionCoursService();

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

        Label label = new Label("✨ Écrivez votre avis sur le cours :");
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
        HBox card = new HBox(20);
        card.getStyleClass().add("course-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));

        // 1. Image du cours
        ImageView imageView = new ImageView(new Image(cours.getImage()));
        imageView.setFitWidth(100);
        imageView.setFitHeight(70);
        imageView.setPreserveRatio(true);

        // 2. Texte : titre, prerequis, catégorie
        VBox textContainer = new VBox(5);
        textContainer.setAlignment(Pos.CENTER_LEFT);

        Label titreLabel = new Label(cours.getNom());
        titreLabel.getStyleClass().add("course-title");

        Label prerequisLabel = new Label("Prérequis : " + cours.getRequirement());
        prerequisLabel.getStyleClass().add("course-description");

        Label categorieLabel = new Label("Catégorie : " + cours.getCategorie().getNom());
        categorieLabel.getStyleClass().add("course-description");

        textContainer.getChildren().addAll(titreLabel, prerequisLabel, categorieLabel);

        // 3. Bouton Avis
        Button avisButton = new Button("📝 Avis");
        avisButton.getStyleClass().add("avis-button");
        avisButton.setOnAction(e -> openAvisPopup(cours));

        // 4. Bouton Certificat
        Button certificatButton = new Button("📜 Certificat");
        certificatButton.getStyleClass().add("certificat-button");
//        certificatButton.setOnAction(e -> generateCertificate(cours));


        User currentUser = UserSession.getInstance().getCurrentUser();
        AvisCours existingAvis = serviceAvisCours.searchAvisByStudentAndCourse(currentUser.getId(), cours.getId());
        if (existingAvis != null){
            avisButton.setDisable(true);
        }

        // 5. Emojis
        Image likeImageFixe = new Image("photos/like_fixe.png");
        Image likeImage = new Image("photos/like.gif");

        Image adoreImageFixe = new Image("photos/adore_fixe.png");
        Image adoreImage = new Image("photos/adore.gif");

        Image disLikeImageFixe = new Image("photos/dislike_fixe.png");
        Image disLikeImage = new Image("photos/dislike.gif");

        ImageView likeEmoji = new ImageView(likeImageFixe);
        ImageView adoreEmoji = new ImageView(adoreImageFixe);
        ImageView disLikeEmoji = new ImageView(disLikeImageFixe);

        // Mouse hover animation
        likeEmoji.setOnMouseEntered(mouseEvent -> {likeEmoji.setImage(likeImage);});
        likeEmoji.setOnMouseExited(mouseEvent -> {likeEmoji.setImage(likeImageFixe);});
        likeEmoji.setOnMouseClicked(mouseEvent -> {
            ReactionCours newReactionCours = new ReactionCours();
            newReactionCours.setCours(cours);
            newReactionCours.setStudent(UserSession.getInstance().getCurrentUser());
            newReactionCours.setReaction("like");
            reactionCoursService.addReaction(newReactionCours);
        });

        adoreEmoji.setOnMouseEntered(mouseEvent -> {adoreEmoji.setImage(adoreImage);});
        adoreEmoji.setOnMouseExited(mouseEvent -> {adoreEmoji.setImage(adoreImageFixe);});
        adoreEmoji.setOnMouseClicked(mouseEvent -> {
            ReactionCours newReactionCours = new ReactionCours();
            newReactionCours.setCours(cours);
            newReactionCours.setStudent(UserSession.getInstance().getCurrentUser());
            newReactionCours.setReaction("adore");
            reactionCoursService.addReaction(newReactionCours);
        });

        disLikeEmoji.setOnMouseEntered(mouseEvent -> {
            disLikeEmoji.setImage(disLikeImage);
            disLikeEmoji.setFitWidth(30);
            disLikeEmoji.setFitHeight(30);
        });
        disLikeEmoji.setOnMouseExited(mouseEvent -> {
            disLikeEmoji.setImage(disLikeImageFixe);
            disLikeEmoji.setFitWidth(30);
            disLikeEmoji.setFitHeight(30);
        });
        disLikeEmoji.setOnMouseClicked(mouseEvent -> {
            ReactionCours newReactionCours = new ReactionCours();
            newReactionCours.setCours(cours);
            newReactionCours.setStudent(UserSession.getInstance().getCurrentUser());
            newReactionCours.setReaction("dislike");
            reactionCoursService.addReaction(newReactionCours);
        });

        likeEmoji.setFitWidth(60);
        likeEmoji.setFitHeight(60);

        adoreEmoji.setFitWidth(60);
        adoreEmoji.setFitHeight(60);

        disLikeEmoji.setFitWidth(60);
        disLikeEmoji.setFitHeight(60);

        // Click reactions
        likeEmoji.setOnMouseClicked(e -> handleReaction(cours, "like"));
        adoreEmoji.setOnMouseClicked(e -> handleReaction(cours, "adore"));
        disLikeEmoji.setOnMouseClicked(e -> handleReaction(cours, "dislike"));

        // Gérer la taille spéciale si déjà réagi
        ReactionCours existingReaction = reactionCoursService.searchReactionByStudentAndCourse(currentUser.getId(), cours.getId());
        if (existingReaction != null) {
            switch (existingReaction.getReaction()) {
                case "like" -> {
                    likeEmoji.setFitHeight(90);
                    likeEmoji.setFitWidth(90);
                }
                case "adore" -> {
                    adoreEmoji.setFitHeight(90);
                    adoreEmoji.setFitWidth(90);
                }
                case "dislike" -> {
                    disLikeEmoji.setFitHeight(45);
                    disLikeEmoji.setFitWidth(45);
                }
            }
        }

        // 6. Container emojis
        HBox emojiHBox = new HBox(10, likeEmoji, adoreEmoji, disLikeEmoji);
        emojiHBox.setAlignment(Pos.CENTER);

        // 7. Container Avis + Emojis
        VBox avisAndEmojisVBox = new VBox(10,emojiHBox,avisButton);
        avisAndEmojisVBox.setAlignment(Pos.CENTER);

        // 8. Container final boutons
        VBox buttonsContainer = new VBox(10, avisAndEmojisVBox, certificatButton);
        buttonsContainer.setAlignment(Pos.CENTER);

        // 9. Spacer pour aligner bien
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 10. Ajouter tout dans la carte
        card.getChildren().addAll(imageView, textContainer, spacer, buttonsContainer);

        // Animation : légère translation + fondu
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), card);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(600), card);
        slideIn.setFromX(50); // Démarre 50px à droite
        slideIn.setToX(0);    // Arrive à sa position normale

        // Jouer les deux animations ensemble
        ParallelTransition parallelTransition = new ParallelTransition(fadeIn, slideIn);
        parallelTransition.play();

        coursContainer.getChildren().add(card);
    }

    // Gère l'ajout de la réaction
    private void handleReaction(Cours cours, String reactionType) {
        ReactionCours newReaction = new ReactionCours();
        newReaction.setCours(cours);
        newReaction.setStudent(UserSession.getInstance().getCurrentUser());
        newReaction.setReaction(reactionType);
        reactionCoursService.addReaction(newReaction);
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
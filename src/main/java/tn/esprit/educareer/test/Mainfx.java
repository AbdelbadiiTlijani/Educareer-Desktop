package tn.esprit.educareer.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.educareer.utils.MyConnection;

public class Mainfx extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReadProjets.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        stage.setTitle("Liste des projet");
        stage.setScene(scene);
        stage.show();







    }
    public static void main(String[] args) {
        launch(args);
    }


}

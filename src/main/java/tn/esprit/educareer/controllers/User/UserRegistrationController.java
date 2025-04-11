package tn.esprit.educareer.controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.io.IOException;

public class UserRegistrationController implements Initializable {

    @FXML
    private Button PhotoButton;

    @FXML
    private TextField PhotoField;

    @FXML
    private DatePicker birthDatePicker;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;


    @FXML
    private Button backButton;
    @FXML
    private ComboBox<String> roleComboBox;

    // Vos autres éléments FXML avec @FXML

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialiser les options du ComboBox
        roleComboBox.getItems().addAll("Formateur", "Étudiant");


    }


}

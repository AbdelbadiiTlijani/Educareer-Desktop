package tn.esprit.educareer.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import tn.esprit.educareer.models.Messages;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ServiceMessages;
import tn.esprit.educareer.utils.UserSession;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.List;

public class ChatBoxController {

    @FXML private TextField emailRecipientField;
    @FXML private TextField titleField;
    @FXML private TextArea messageField;
    @FXML private VBox messagesContainer;

    private int senderId = UserSession.getInstance().getCurrentUser().getId();
    private int recipientId = -1;

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private final ServiceMessages sm = ServiceMessages.getInstance();

    public void initialize() {
        connectSocket();
        listenForMessages();
    }

    public void connectSocket() {
        try {
            socket = new Socket("localhost", 8888);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Socket connection established!");
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Error: Could not establish connection to server.", false);
        }
    }

    public void listenForMessages() {
        new Thread(() -> {
            String msg;
            try {
                while ((msg = reader.readLine()) != null) {
                    String[] parts = msg.split("::", 3);
                    if (parts.length == 3) {
                        String senderName = parts[0];
                        String title = parts[1];
                        String content = parts[2];
                        boolean isSelf = senderName.equals(UserSession.getInstance().getCurrentUser().getNom());
                        showMessage(senderName, title, content, isSelf);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("Error: Unable to receive messages.", false);
            }
        }).start();
    }

    private void showMessage(String senderName, String title, String msg, boolean isSelf) {
        Platform.runLater(() -> {
            Label nameLabel = new Label( senderName);
            nameLabel.setStyle("-fx-font-weight: bold;");

            Label titleLabel = new Label("Titre: " + title);
            titleLabel.setStyle("-fx-background-color: " + (isSelf ? "#cce5ff" : "#d4edda") + ";"
                    + "-fx-padding: 4; -fx-background-radius: 6;");

            Label messageLabel = new Label("Message: " + msg);
            messageLabel.setWrapText(true);
            messageLabel.setStyle("-fx-background-color: " + (isSelf ? "#cce5ff" : "#d4edda") + ";"
                    + "-fx-padding: 4; -fx-background-radius: 6;");

            VBox messageBox = new VBox(nameLabel, titleLabel, messageLabel);
            messageBox.setAlignment(isSelf ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            messageBox.setSpacing(2);

            messagesContainer.getChildren().add(messageBox);
        });
    }

    private void showMessage(String msg, boolean isSelf) {
        Platform.runLater(() -> {
            Label label = new Label(msg);
            label.setWrapText(true);
            label.setStyle("-fx-background-color: " + (isSelf ? "#cce5ff" : "#d4edda") + ";"
                    + "-fx-padding: 8; -fx-background-radius: 8;");
            HBox container = new HBox(label);
            container.setAlignment(isSelf ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            messagesContainer.getChildren().add(container);
        });
    }

    @FXML
    public void handleSearchRecipient() {
        String email = emailRecipientField.getText().trim();
        User user = sm.getUserByEmail(email);
        if (user != null) {
            recipientId = user.getId();
            showMessage("Recipient found: " + user.getNom() + " " + user.getPrenom(), false);
            showMessage("---------------------------------------------------", false);
            loadMessages();
        } else {
            showMessage("Recipient not found.", false);
        }
    }

    @FXML
    public void handleSendMessage() {
        if (recipientId == -1) {
            showMessage("Please search and select a recipient first.", false);
            return;
        }

        String titre = titleField.getText().trim();
        String msg = messageField.getText().trim();
        String nomExpediteur = UserSession.getInstance().getCurrentUser().getNom();

        if (titre.isEmpty() || msg.isEmpty()) {
            showMessage("Le titre et le message ne doivent pas être vides.", false);
            return;
        }

        if (writer == null) {
            showMessage("Error: No connection to server.", false);
            return;
        }

        try {
            // Envoi du message
            writer.write(nomExpediteur + "::" + titre + "::" + msg);
            writer.newLine();
            writer.flush();

            // Sauvegarde base de données
            Messages message = new Messages(senderId, recipientId, titre, msg, new Date(), false);
            sm.addMessage(message);

            // Réinitialisation des champs
            titleField.clear();
            messageField.clear();
            showMessage("Message sent!", true);

        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Error: Unable to send message.", false);
        }
    }

    private void loadMessages() {
        if (recipientId == -1) return;

        List<Messages> messages = sm.getMessages(senderId, recipientId);
        for (Messages message : messages) {
            boolean isSelf = message.getSender_id() == senderId;
            User sender = sm.getUserById(message.getSender_id());
            String senderName = sender != null ? sender.getNom() : "Inconnu";
            showMessage(senderName, message.getTitre(), message.getMessage(), isSelf);
        }
    }
}

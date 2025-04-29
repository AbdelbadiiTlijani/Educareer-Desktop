package tn.esprit.educareer.controllers.Event;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.IOException;

public class ChatbotController {

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyCa8Z-PHVGzxRlSEYRRwJzVuRL2EaVD7P0";
    private static final OkHttpClient client = new OkHttpClient();

    @FXML
    public void initialize() {
        sendButton.setOnAction(event -> handleSendMessage());
    }
@FXML
    private void handleSendMessage() {
        String message = userInput.getText();
        if (message.isEmpty()) return;

        appendToChat("Vous: " + message);
        userInput.clear();

        sendToGemini(message);
    }

    private void appendToChat(String text) {
        chatArea.appendText(text + "\n");
        chatArea.setStyle("-fx-text-fill: #4CAF50;");

    }

    private void sendToGemini(String userMessage) {
        String jsonRequest = """
        {
          "contents": [{
            "parts": [{"text": "%s"}]
          }]
        }
        """.formatted(userMessage);

        RequestBody body = RequestBody.create(
                jsonRequest,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                javafx.application.Platform.runLater(() -> appendToChat("Erreur de connexion à Gemini API."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    javafx.application.Platform.runLater(() -> appendToChat("Erreur de la réponse API: " + response.code()));
                    return;
                }

                String responseBody = response.body().string();
                String reply = extractReply(responseBody);

                javafx.application.Platform.runLater(() -> appendToChat("Gemini: " + reply));
            }
        });
    }

    private String extractReply(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            JSONArray candidates = json.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject firstCandidate = candidates.getJSONObject(0);
                JSONObject content = firstCandidate.getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                if (parts.length() > 0) {
                    return parts.getJSONObject(0).getString("text");
                }
            }
            return "Pas de contenu trouvé.";
        } catch (Exception e) {
            return "Erreur de parsing de réponse.";
        }
    }
}

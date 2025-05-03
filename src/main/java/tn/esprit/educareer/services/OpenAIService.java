package tn.esprit.educareer.services;

import okhttp3.*;

import java.io.IOException;

public class OpenAIService {
    private static final String API_KEY = "91ebd7b24cmsh0e449e50ed7c5a6p1dd1b5jsn82e4105a37b6";
    private static final String API_URL = "https://openai37.p.rapidapi.com/chat-completion";
    private final OkHttpClient client;

    public OpenAIService() {
        this.client = new OkHttpClient();
    }

    public String getChatbotResponse(String prompt) throws IOException {
        String jsonBody = "{\n" +
                "  \"model\": \"text-davinci-003\",\n" +
                "  \"prompt\": \"" + prompt + "\",\n" +
                "  \"max_tokens\": 150,\n" +
                "  \"temperature\": 0.7\n" +
                "}";

        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            return extractAnswerFromResponse(responseBody);
        }
    }

    private String extractAnswerFromResponse(String responseBody) {
        int start = responseBody.indexOf("\"text\": \"") + 9;
        int end = responseBody.indexOf("\"}", start);
        return responseBody.substring(start, end).trim();
    }
}
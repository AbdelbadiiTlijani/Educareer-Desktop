package tn.esprit.educareer.services;

import okhttp3.*;

import java.io.IOException;

public class WhatsappService {

    private static final String API_URL = "https://9k25r4.api.infobip.com/whatsapp/1/message/template";
    private static final String API_KEY = "App 8bc1021e9e1ed84c13422456aaa5d1b6-db810d0d-a432-4d16-ac7b-a5ae84f4ec7c"; // Ton vrai API key ici
    private static final String FROM_NUMBER = "447860099299"; // Le numéro Infobip que tu utilises pour envoyer

    private final OkHttpClient client;

    public WhatsappService() {
        client = new OkHttpClient();
    }

    public void sendWhatsappMessage(String toNumber, String eventName) {
        MediaType mediaType = MediaType.parse("application/json");

        // Message personnalisé
        String personalizedMessage = "Un nouvel événement sous le nom \"" + eventName + "\" a été ajouté avec succès.";


        String jsonBody = "{\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"from\": \"" + FROM_NUMBER + "\",\n" +
                "      \"to\": \"" + toNumber + "\",\n" +
                "      \"messageId\": \"" + java.util.UUID.randomUUID() + "\",\n" + // Générer un ID unique
                "      \"content\": {\n" +
                "        \"templateName\": \"test_whatsapp_template_en\",\n" + // Le template configuré dans Infobip
                "        \"templateData\": {\n" +
                "          \"body\": {\n" +
                "            \"placeholders\": [\"" + eventName + "\"]\n" +
                "          }\n" +
                "        },\n" +
                "        \"language\": \"en\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        RequestBody body = RequestBody.create(mediaType, jsonBody);

        Request request = new Request.Builder()
                .url(API_URL)
                .method("POST", body)
                .addHeader("Authorization", API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Message WhatsApp envoyé avec succès !");
            } else {
                System.out.println("Erreur lors de l'envoi du message : " + response.code() + " " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

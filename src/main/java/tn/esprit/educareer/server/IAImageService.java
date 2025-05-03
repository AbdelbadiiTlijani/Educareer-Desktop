package tn.esprit.educareer.server;

import java.io.*;
import java.net.*;
import org.json.*;

public class IAImageService {
    private static final String API_URL = "https://picsum.photos/200/300";  // URL pour une image aléatoire de 200x300

    public static String getRandomImage() throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Lecture de la réponse
        StringBuilder response;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // L'URL d'une image aléatoire est directement l'URL d'accès à l'image
        return connection.getURL().toString();  // Retourne l'URL de l'image
    }
}

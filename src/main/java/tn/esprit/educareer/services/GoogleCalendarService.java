package tn.esprit.educareer.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "projetjava";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String SERVICE_ACCOUNT_KEY_PATH = "src/main/resources/credentials.json"; // Ton fichier JSON service account
    private static final String CALENDAR_ID = "projetjava@mindful-future-452318-q2.iam.gserviceaccount.com"; // Utilise 'primary' pour le calendrier principal, sinon utilise un ID spécifique

    // Fonction pour obtenir le service Google Calendar
    private static Calendar getCalendarService() throws IOException, GeneralSecurityException {
        // Charger les credentials à partir du fichier service account
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

        // Retourner l'objet Calendar avec les credentials
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    // Fonction pour ajouter un événement au calendrier Google
    public static void addEventToCalendar(String summary, String description, LocalDateTime eventDate) {
        try {
            Calendar service = getCalendarService();

            // Convertir LocalDateTime en Date pour la date de début
            Date startDate = Date.from(eventDate.atZone(ZoneId.systemDefault()).toInstant());

            // Créer un nouvel événement
            Event event = new Event()
                    .setSummary(summary)
                    .setDescription(description)
                    .setLocation("Tunisie");

            // Définir la date de début de l'événement
            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(startDate))
                    .setTimeZone("Africa/Tunis");
            event.setStart(start);

            // La date de fin est la même que la date de début (pas de date de fin distincte)
            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(startDate))
                    .setTimeZone("Africa/Tunis");
            event.setEnd(end);
            String calendarId = "tlili6085@gmail.com"; // Important
            service.events().insert(calendarId, event).execute();



            // Ajouter l'événement au calendrier
            event = service.events().insert(CALENDAR_ID, event).execute();

            // Si l'événement est ajouté avec succès, afficher les détails
            System.out.println("Événement ajouté avec succès au calendrier Google!");
            System.out.println("ID de l'événement : " + event.getId());
            System.out.println("Résumé : " + event.getSummary());
            System.out.println("Date de début : " + event.getStart().getDateTime());

        } catch (IOException | GeneralSecurityException e) {
            // Afficher l'erreur détaillée
            System.out.println("Erreur lors de l'ajout de l'événement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Fonction pour vérifier les événements à venir dans le calendrier
    public static void checkCalendarConnection() {
        try {
            Calendar service = getCalendarService();

            // Récupérer les 10 premiers événements à venir
            Calendar.Events.List request = service.events().list(CALENDAR_ID);
            request.setMaxResults(10);
            request.setOrderBy("startTime");
            request.setSingleEvents(true);

            var events = request.execute();

            if (events.getItems().isEmpty()) {
                System.out.println("Aucun événement à venir.");
            } else {
                System.out.println("Événements à venir :");
                for (Event event : events.getItems()) {
                    System.out.println(event.getSummary() + " (" + event.getStart().getDateTime() + ")");
                }
            }
        } catch (IOException | GeneralSecurityException e) {
            System.out.println("Erreur lors de la récupération des événements : " + e.getMessage());
        }
    }
}

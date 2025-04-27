package tn.esprit.educareer.services;

import tn.esprit.educareer.interfaces.IService;
import tn.esprit.educareer.models.Event;
import tn.esprit.educareer.models.TypeEvent;
import tn.esprit.educareer.utils.MyConnection;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvent implements IService<Event> {
    private final Connection cnx;

    public ServiceEvent() {
        cnx = MyConnection.getInstance().getCnx();
    }

    public void ajouter(Event event) {
        // Créer une instance de WhatsAppService
        WhatsappService whatsappService = new WhatsappService();

        // Requête d'insertion de l'événement
        String req = "INSERT INTO event (type_event_id, titre, description, date, lieu, nbr_place) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            // Préparer la requête SQL
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, event.getTypeEvent().getId());  // ID du type d'événement
            pst.setString(2, event.getTitre());           // Titre de l'événement
            pst.setString(3, event.getDescription());     // Description de l'événement
            pst.setTimestamp(4, Timestamp.valueOf(event.getDate()));  // Date de l'événement
            pst.setString(5, event.getLieu());            // Lieu de l'événement
            pst.setInt(6, event.getNbrPlace());           // Nombre de places de l'événement

            // Exécuter la requête
            pst.executeUpdate();
            System.out.println("Événement ajouté !");

            // Envoyer le message WhatsApp avec le titre de l'événement
            whatsappService.sendWhatsappMessage("21698479767", event.getTitre());  // Numéro et titre de l'événement

        } catch (SQLException e) {
            System.out.println("Erreur d'ajout : " + e.getMessage());
        }
    }




    @Override
    public void modifier(Event event) {
        String req = "UPDATE event SET type_event_id = ?, titre = ?, description = ?, date = ?, lieu = ?, nbr_place = ? WHERE id = ?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, event.getTypeEvent().getId());
            pst.setString(2, event.getTitre());
            pst.setString(3, event.getDescription());
            pst.setTimestamp(4, Timestamp.valueOf(event.getDate()));
            pst.setString(5, event.getLieu());
            pst.setInt(6, event.getNbrPlace());
            pst.setInt(7, event.getId());
            pst.executeUpdate();
            System.out.println(" Événement modifié !");
        } catch (SQLException e) {
            System.out.println(" Erreur de modification : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Event event) {
        String req = "DELETE FROM event WHERE id = ?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, event.getId());
            pst.executeUpdate();
            System.out.println(" Événement supprimé !");
        } catch (SQLException e) {
            System.out.println("Erreur de suppression : " + e.getMessage());
        }
    }

    @Override
    public List<Event> getAll() {
        List<Event> events = new ArrayList<>();
        String req = "SELECT e.*, t.nom_e FROM event e JOIN type_event t ON e.type_event_id = t.id";

        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                TypeEvent typeEvent = new TypeEvent(
                        rs.getInt("type_event_id"),
                        rs.getString("nom_e")
                );

                Event e = new Event(
                        rs.getInt("id"),
                        typeEvent,
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getString("lieu"),
                        rs.getInt("nbr_place")
                );

                events.add(e);
            }
        } catch (SQLException e) {
            System.out.println(" Erreur lors de la récupération : " + e.getMessage());
        }

        return events;
    }

    @Override
    public Event getOne(Event event) {
        return null;
    }
}

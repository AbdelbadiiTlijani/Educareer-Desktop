package tn.esprit.educareer.services;

import tn.esprit.educareer.interfaces.IService;
import tn.esprit.educareer.models.Event;
import tn.esprit.educareer.models.TypeEvent;
import tn.esprit.educareer.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvent implements IService<Event> {

    private final Connection cnx;

    public ServiceEvent() {
        cnx = MyConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Event event) {
        String req = "INSERT INTO event (type_event_id, titre, description, date, lieu, nbr_place) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {

            if (cnx == null) {
                System.out.println("Erreur : Connexion base de données est nulle !");
                return;
            }

            pst.setInt(1, event.getTypeEvent().getId());
            pst.setString(2, event.getTitre());
            pst.setString(3, event.getDescription());
            pst.setTimestamp(4, Timestamp.valueOf(event.getDate()));
            pst.setString(5, event.getLieu());
            pst.setInt(6, event.getNbrPlace());

            pst.executeUpdate();
            System.out.println("Événement ajouté dans la base de données !");

            try {
                GoogleCalendarService.addEventToCalendar(
                        event.getTitre(),
                        event.getDescription(),
                        event.getDate()
                );
                System.out.println("Événement ajouté dans Google Calendar !");
            } catch (Exception ex) {
                System.out.println("Erreur Google Calendar : " + ex.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Erreur ajout événement : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Event event) {
        String reqEvent = "UPDATE event SET type_event_id = ?, titre = ?, description = ?, date = ?, lieu = ?, nbr_place = ? WHERE id = ?";

        try (PreparedStatement pstEvent = cnx.prepareStatement(reqEvent)) {
            pstEvent.setInt(1, event.getTypeEvent().getId());
            pstEvent.setString(2, event.getTitre());
            pstEvent.setString(3, event.getDescription());
            pstEvent.setTimestamp(4, Timestamp.valueOf(event.getDate()));
            pstEvent.setString(5, event.getLieu());
            pstEvent.setInt(6, event.getNbrPlace());
            pstEvent.setInt(7, event.getId());

            pstEvent.executeUpdate();
            System.out.println("Événement modifié avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur modification événement : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Event event) {
        String req = "DELETE FROM event WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, event.getId());
            pst.executeUpdate();
            System.out.println("Événement supprimé !");
        } catch (SQLException e) {
            System.out.println("Erreur suppression événement : " + e.getMessage());
        }
    }

    @Override
    public List<Event> getAll() {
        List<Event> events = new ArrayList<>();
        String req = "SELECT e.*, t.nom_e FROM event e JOIN type_event t ON e.type_event_id = t.id";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {

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
            System.out.println("Erreur récupération événements : " + e.getMessage());
        }

        return events;
    }

    @Override
    public Event getOne(Event event) {
        String req = "SELECT e.*, t.nom_e FROM event e JOIN type_event t ON e.type_event_id = t.id WHERE e.id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, event.getId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                TypeEvent typeEvent = new TypeEvent(
                        rs.getInt("type_event_id"),
                        rs.getString("nom_e")
                );

                return new Event(
                        rs.getInt("id"),
                        typeEvent,
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getString("lieu"),
                        rs.getInt("nbr_place")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur récupération événement : " + e.getMessage());
        }
        return null;
    }

    public boolean participer(int userId, int eventId) {
        try {
            // Vérification du rôle de l'utilisateur
            String checkUserRole = "SELECT role FROM users WHERE id = ?";
            PreparedStatement pstCheckUser = cnx.prepareStatement(checkUserRole);
            pstCheckUser.setInt(1, userId);
            ResultSet rsUser = pstCheckUser.executeQuery();
            if (rsUser.next()) {
                if (!"student".equalsIgnoreCase(rsUser.getString("role"))) {
                    System.out.println("Utilisateur non autorisé à participer.");
                    return false;
                }
            } else {
                System.out.println("Utilisateur non trouvé.");
                return false;
            }

            // Vérification si l'utilisateur est déjà inscrit
            String checkParticipation = "SELECT * FROM student_event WHERE student_id = ? AND event_id = ?";
            PreparedStatement pstCheckParticipation = cnx.prepareStatement(checkParticipation);
            pstCheckParticipation.setInt(1, userId);
            pstCheckParticipation.setInt(2, eventId);
            ResultSet rsParticipation = pstCheckParticipation.executeQuery();
            if (rsParticipation.next()) {
                System.out.println("Déjà inscrit !");
                return false;
            }

            // Vérification des places disponibles
            String checkEventQuery = "SELECT nbr_place FROM event WHERE id = ?";
            PreparedStatement pstCheckEvent = cnx.prepareStatement(checkEventQuery);
            pstCheckEvent.setInt(1, eventId);
            ResultSet rsEvent = pstCheckEvent.executeQuery();
            if (rsEvent.next()) {
                int remainingPlaces = rsEvent.getInt("nbr_place");
                if (remainingPlaces > 0) {
                    // Inscription
                    String insertQuery = "INSERT INTO student_event (student_id, event_id) VALUES (?, ?)";
                    PreparedStatement pstInsert = cnx.prepareStatement(insertQuery);
                    pstInsert.setInt(1, userId);
                    pstInsert.setInt(2, eventId);
                    pstInsert.executeUpdate();

                    // Décrémentation des places
                    String updateEventQuery = "UPDATE event SET nbr_place = ? WHERE id = ?";
                    PreparedStatement pstUpdateEvent = cnx.prepareStatement(updateEventQuery);
                    pstUpdateEvent.setInt(1, remainingPlaces - 1);
                    pstUpdateEvent.setInt(2, eventId);
                    pstUpdateEvent.executeUpdate();

                    System.out.println("Participation réussie !");
                    return true;
                } else {
                    System.out.println("Plus de places disponibles !");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur participation : " + e.getMessage());
        }
        return false;
    }
}

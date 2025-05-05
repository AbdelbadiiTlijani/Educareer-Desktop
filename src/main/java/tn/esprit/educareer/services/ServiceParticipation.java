package tn.esprit.educareer.services;

import tn.esprit.educareer.utils.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceParticipation {
    private Connection cnx;

    public ServiceParticipation() {
        cnx = MyConnection.getInstance().getCnx();
    }

    public boolean isParticipated(int eventId, int studentId) {
        String sql = "SELECT COUNT(*) FROM event_user WHERE event_id = ? AND user_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Si l'étudiant est déjà inscrit à cet événement
            }
        } catch (SQLException e) {
            System.out.println("Erreur isParticipated: " + e.getMessage());
        }
        return false;
    }

    // Ajout d'une méthode pour récupérer tous les événements auxquels un étudiant est inscrit
    public List<Integer> getParticipatedEventIds(int studentId) {
        List<Integer> eventIds = new ArrayList<>();
        String sql = "SELECT event_id FROM event_user WHERE user_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                eventIds.add(rs.getInt("event_id"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getParticipatedEventIds: " + e.getMessage());
        }

        return eventIds;
    }

    public void participate(int eventId, int studentId) {
        // 1. Vérifier si l'utilisateur existe dans la table 'user'
        String checkUserQuery = "SELECT id FROM user WHERE id = ?";
        try (PreparedStatement psCheckUser = cnx.prepareStatement(checkUserQuery)) {
            psCheckUser.setInt(1, studentId);
            ResultSet rsUser = psCheckUser.executeQuery();
            if (!rsUser.next()) {
                System.out.println("Erreur : L'utilisateur avec l'ID " + studentId + " n'existe pas.");
                return;  // L'utilisateur n'existe pas
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de l'utilisateur : " + e.getMessage());
            return;
        }

        // 2. Vérifier si l'événement existe
        String checkEventQuery = "SELECT id, nbr_place FROM event WHERE id = ?";
        int remainingPlaces = 0;
        try (PreparedStatement psCheckEvent = cnx.prepareStatement(checkEventQuery)) {
            psCheckEvent.setInt(1, eventId);
            ResultSet rsEvent = psCheckEvent.executeQuery();
            if (!rsEvent.next()) {
                System.out.println("Erreur : L'événement avec l'ID " + eventId + " n'existe pas.");
                return;  // L'événement n'existe pas
            }
            remainingPlaces = rsEvent.getInt("nbr_place");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de l'événement : " + e.getMessage());
            return;
        }

        // 3. Vérifier s'il reste des places disponibles
        if (remainingPlaces <= 0) {
            System.out.println("Erreur : Il n'y a plus de places disponibles pour cet événement.");
            return;  // Pas de places disponibles
        }

        // 4. Vérifier si l'utilisateur est déjà inscrit à cet événement
        String checkParticipation = "SELECT COUNT(*) FROM event_user WHERE event_id = ? AND user_id = ?";
        try (PreparedStatement psCheckParticipation = cnx.prepareStatement(checkParticipation)) {
            psCheckParticipation.setInt(1, eventId);
            psCheckParticipation.setInt(2, studentId);
            ResultSet rsParticipation = psCheckParticipation.executeQuery();
            if (rsParticipation.next() && rsParticipation.getInt(1) > 0) {
                System.out.println("Erreur : Vous êtes déjà inscrit à cet événement.");
                return;  // L'utilisateur est déjà inscrit
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de la participation : " + e.getMessage());
            return;
        }

        // 5. Inscrire l'utilisateur à l'événement
        String insertParticipationQuery = "INSERT INTO event_user (event_id, user_id) VALUES (?, ?)";
        try (PreparedStatement psInsertParticipation = cnx.prepareStatement(insertParticipationQuery)) {
            psInsertParticipation.setInt(1, eventId);
            psInsertParticipation.setInt(2, studentId);
            psInsertParticipation.executeUpdate();
            System.out.println("Participation réussie à l'événement.");

            // 6. Décrémenter le nombre de places disponibles
            String updateEventQuery = "UPDATE event SET nbr_place = ? WHERE id = ?";
            try (PreparedStatement psUpdateEvent = cnx.prepareStatement(updateEventQuery)) {
                psUpdateEvent.setInt(1, remainingPlaces - 1);
                psUpdateEvent.setInt(2, eventId);
                psUpdateEvent.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'inscription à l'événement : " + e.getMessage());
        }
    }
}
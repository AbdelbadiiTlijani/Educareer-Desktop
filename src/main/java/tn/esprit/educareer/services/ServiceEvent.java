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
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, event.getTypeEvent().getId());
            pst.setString(2, event.getTitre());
            pst.setString(3, event.getDescription());
            pst.setDate(4, Date.valueOf(event.getDate()));
            pst.setString(5, event.getLieu());
            pst.setInt(6, event.getNbrPlace());
            pst.executeUpdate();
            System.out.println(" Événement ajouté !");
        } catch (SQLException e) {
            System.out.println(" Erreur d'ajout : " + e.getMessage());
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
            pst.setDate(4, Date.valueOf(event.getDate()));
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
                        rs.getDate("date").toLocalDate(),
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
        // Optionnel : implémenter si besoin
        return null;
    }
}

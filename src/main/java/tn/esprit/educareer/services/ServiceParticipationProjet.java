package tn.esprit.educareer.services;

import tn.esprit.educareer.interfaces.IService;
import tn.esprit.educareer.models.ParticipationProjet;
import tn.esprit.educareer.utils.MyConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceParticipationProjet implements IService<ParticipationProjet> {
    Connection cnx;

    public ServiceParticipationProjet() {
        cnx = MyConnection.getInstance().getCnx();
    }

    public void ajouter(ParticipationProjet participationProjet) {
        try {
            // Vérifier si la participation existe déjà
            String checkQuery = "SELECT 1 FROM participation_projet WHERE student_id = ? AND projet_id = ?";
            PreparedStatement checkStmt = cnx.prepareStatement(checkQuery);
            checkStmt.setInt(1, participationProjet.getStudent_id());
            checkStmt.setInt(2, participationProjet.getProjet_id());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Il y a déjà une participation avec cet étudiant et ce projet
                System.out.println("Cette participation existe déjà !");
                return;
            }

            // Insertion si pas de doublon
            String req = "INSERT INTO participation_projet (student_id, projet_id, date_participation) VALUES (?, ?, ?)";
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, participationProjet.getStudent_id());
            ps.setInt(2, participationProjet.getProjet_id());
            ps.setDate(3, java.sql.Date.valueOf(participationProjet.getDate_participation()));

            ps.executeUpdate();
            System.out.println("Participation ajoutée avec succès !");
        } catch (SQLException ex) {
            System.out.println("Erreur lors de l'ajout de participation : " + ex.getMessage());
        }
    }


    public boolean existeDeja(int studentId, int projetId) {
        try {
            String req = "SELECT COUNT(*) FROM participation_projet WHERE student_id = ? AND projet_id = ?";
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, studentId);
            ps.setInt(2, projetId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // true si déjà existant
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la vérification d'existence : " + ex.getMessage());
        }
        return false;
    }

    public void participerSiPasExiste(int studentId, int projetId) {
        if (!existeDeja(studentId, projetId)) {
            ParticipationProjet participation = new ParticipationProjet();
            participation.setStudent_id(studentId);
            participation.setProjet_id(projetId);
            participation.setDate_participation(LocalDate.now());
            ajouter(participation);
        } else {
            System.out.println("Déjà participant, aucune insertion.");
        }
    }

    @Override
    public void modifier(ParticipationProjet participationProjet) {
        // pas nécessaire pour ton besoin maintenant
    }

    @Override
    public void supprimer(ParticipationProjet participationProjet) {
        // pas nécessaire pour ton besoin maintenant
    }

    @Override
    public List<ParticipationProjet> getAll() {
        List<ParticipationProjet> participations = new ArrayList<>();
        try {
            String req = "SELECT * FROM participation_projet";
            PreparedStatement pst = cnx.prepareStatement(req);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                ParticipationProjet p = new ParticipationProjet();
                p.setId(rs.getInt("id"));
                p.setStudent_id(rs.getInt("student_id"));
                p.setProjet_id(rs.getInt("projet_id"));
                p.setDate_participation(rs.getDate("date_participation").toLocalDate());

                participations.add(p);
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors du getAll : " + ex.getMessage());
        }
        return participations;
    }

    @Override
    public ParticipationProjet getOne(ParticipationProjet participationProjet) {
        return null;
    }


    public Map<LocalDate, Integer> getParticipantsCountByDate() {
        Map<LocalDate, Integer> data = new HashMap<>();
        String query = "SELECT date_participation, COUNT(*) AS participants_count FROM participation_projet GROUP BY date_participation ORDER BY date_participation";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                LocalDate date = rs.getDate("date_participation").toLocalDate();
                int count = rs.getInt("participants_count");
                data.put(date, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

}

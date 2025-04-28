package tn.esprit.educareer.services;

import tn.esprit.educareer.models.ReactionCours;
import tn.esprit.educareer.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReactionCoursService {

    Connection cnx;
    ServiceUser serviceUser = new ServiceUser();
    ServiceCours serviceCours = new ServiceCours();

    public ReactionCoursService() {
        cnx = MyConnection.getInstance().getCnx();
    }

    // Method to search for a ReactionCours by studentId and coursId
    public ReactionCours searchReactionByStudentAndCourse(int studentId, int courseId) {
        String query = "SELECT * FROM ReactionCours WHERE student_id = ? AND cours_id = ?";
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new ReactionCours(rs.getInt("id"), serviceCours.getOneById(rs.getInt("cours_id")), serviceUser.getOneById(rs.getInt("student_id")), rs.getString("reaction"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // No reaction found
    }

    // Modified addReaction method to check if reaction already exists
    public void addReaction(ReactionCours reaction) {
        try {
            // Check if the reaction already exists for the given student and course
            ReactionCours existingReaction = searchReactionByStudentAndCourse(reaction.getStudent().getId(), reaction.getCours().getId());

            // If a reaction already exists, remove it
            if (existingReaction != null) {
                removeReaction(existingReaction);
            }

            // Insert the new reaction
            String sql = "INSERT INTO ReactionCours (cours_id, student_id, reaction) VALUES (?, ?, ?)";
            PreparedStatement statement = cnx.prepareStatement(sql);
            statement.setInt(1, reaction.getCours().getId());
            statement.setInt(2, reaction.getStudent().getId());
            statement.setString(3, reaction.getReaction());
            statement.executeUpdate();
            System.out.println("Reaction ajouté avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to remove a reaction from the database
    private void removeReaction(ReactionCours reaction) {
        String sql = "DELETE FROM ReactionCours WHERE id = ?";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setInt(1, reaction.getId());
            statement.executeUpdate();
            System.out.println("Ancienne réaction supprimée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ReactionCours> getReactionsForCourse(int courseId) {
        List<ReactionCours> reactionCoursList = new ArrayList<>();
        String req = "SELECT * FROM reaction_cours";
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                ReactionCours c = new ReactionCours(rs.getInt("id"), serviceCours.getOneById(rs.getInt("cours_id")), serviceUser.getOneById(rs.getInt("student_id")), rs.getString("reaction"));
                reactionCoursList.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des cours : " + e.getMessage());
        }
        return reactionCoursList;
    }
}

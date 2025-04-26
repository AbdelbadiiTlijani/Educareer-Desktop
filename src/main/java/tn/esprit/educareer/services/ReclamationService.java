package tn.esprit.educareer.services;

import tn.esprit.educareer.models.Reclamation;
import tn.esprit.educareer.models.TypeReclamation;
import tn.esprit.educareer.utils.MyConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService {
    private Connection cnx;

    public ReclamationService() {
        cnx = MyConnection.getInstance().getCnx();
    }

    // Récupérer toutes les réclamations avec leur type
    public List<Reclamation> getAll() {
        List<Reclamation> list = new ArrayList<>();
        String req = "SELECT r.*, t.nom AS type_nom, t.description AS type_description " +
                "FROM reclamation r " +
                "JOIN type_reclamation t ON r.type_reclamation_id = t.id";

        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);

            while (rs.next()) {
                Reclamation r = new Reclamation();
                r.setId(rs.getInt("id"));
                r.setSujet(rs.getString("sujet"));
                r.setDescription(rs.getString("description"));

                // Conversion du Timestamp SQL en LocalDateTime Java
                Timestamp ts = rs.getTimestamp("created_at");
                LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : null;
                r.setCreatedAt(createdAt);

                TypeReclamation type = new TypeReclamation(
                        rs.getInt("type_reclamation_id"),
                        rs.getString("type_nom"),
                        rs.getString("type_description")
                );

                r.setTypeReclamation(type);
                list.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll : " + e.getMessage());
        }

        return list;
    }

    // Récupérer toutes les catégories de type réclamation
    public List<TypeReclamation> getAllTypes() {
        List<TypeReclamation> list = new ArrayList<>();
        String req = "SELECT * FROM type_reclamation";

        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);

            while (rs.next()) {
                TypeReclamation t = new TypeReclamation(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAllTypes : " + e.getMessage());
        }

        return list;
    }

    // Supprimer une réclamation
    public void supprimer(Reclamation r) {
        String req = "DELETE FROM reclamation WHERE id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur suppression : " + e.getMessage());
        }
    }

    public void ajouter(Reclamation reclamation) {
        String query = "INSERT INTO reclamation (type_reclamation_id, sujet, description, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, reclamation.getTypeReclamation().getId());
            pst.setString(2, reclamation.getSujet());
            pst.setString(3, reclamation.getDescription());
            pst.setTimestamp(4, Timestamp.valueOf(reclamation.getCreatedAt()));
            pst.executeUpdate();
            System.out.println("Réclamation ajoutée !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

}


package tn.esprit.educareer.services;

import tn.esprit.educareer.models.TypeReclamation;
import tn.esprit.educareer.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeReclamationService {
    private Connection cnx;

    public TypeReclamationService() {
        cnx = MyConnection.getInstance().getCnx();
    }

    public void ajouter(TypeReclamation type) {
        String req = "INSERT INTO type_reclamation (nom, description) VALUES (?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setString(1, type.getNom());
            pst.setString(2, type.getDescription());
            pst.executeUpdate();
            System.out.println("Type de réclamation ajouté !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    public void supprimer(int id) {
        String req = "DELETE FROM type_reclamation WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Type de réclamation supprimé !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    public List<TypeReclamation> getAll() {
        List<TypeReclamation> list = new ArrayList<>();
        String req = "SELECT * FROM type_reclamation";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                TypeReclamation type = new TypeReclamation(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description")
                );
                list.add(type);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération : " + e.getMessage());
        }

        return list;
    }

    public List<TypeReclamation> searchByName(String name) {
        List<TypeReclamation> result = new ArrayList<>();
        String req = "SELECT * FROM type_reclamation WHERE nom LIKE ?";

        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setString(1, "%" + name + "%"); // Recherche par sous-chaîne dans le nom
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    TypeReclamation type = new TypeReclamation(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("description")
                    );
                    result.add(type);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche : " + e.getMessage());
        }

        return result;
    }
}

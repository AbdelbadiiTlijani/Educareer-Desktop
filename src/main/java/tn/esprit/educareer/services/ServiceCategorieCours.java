package tn.esprit.educareer.services;

import tn.esprit.educareer.interfaces.IService;
import tn.esprit.educareer.models.CategorieCours;
import tn.esprit.educareer.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCategorieCours implements IService<CategorieCours> {
    Connection cnx;

    public ServiceCategorieCours() {
        cnx = MyConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(CategorieCours categorieCours) {
        String req = "INSERT INTO categorie_cours (nom) VALUES (?)";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, categorieCours.getNom());
            pst.executeUpdate();
            System.out.println("Catégorie cours ajoutée avec succès !");
        } catch (SQLException ex) {
            System.out.println("Erreur lors de l'ajout : " + ex.getMessage());
        }
    }

    @Override
    public void modifier(CategorieCours categorieCours) {
        String req = "UPDATE categorie_cours SET nom=? WHERE id=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, categorieCours.getNom());
            pst.setInt(2, categorieCours.getId());
            pst.executeUpdate();
            System.out.println("Catégorie cours modifiée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(CategorieCours categorieCours) {
        String req = "DELETE FROM categorie_cours WHERE id=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, categorieCours.getId());
            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Catégorie '" + categorieCours.getNom() + "' (ID: " + categorieCours.getId() + ") supprimée avec succès.");
            } else {
                System.out.println("Aucune catégorie trouvée avec l'ID: " + categorieCours.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public List<CategorieCours> getAll() {
        List<CategorieCours> categories = new ArrayList<>();
        String req = "SELECT * FROM categorie_cours";

        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);

            while (rs.next()) {
                CategorieCours c = new CategorieCours(rs.getInt("id"), rs.getString("nom"));
                categories.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des catégories : " + e.getMessage());
        }
        return categories;
    }

    @Override
    public CategorieCours getOne(CategorieCours categorieCours) {
        String req = "SELECT * FROM categorie_cours WHERE id=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, categorieCours.getId());
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new CategorieCours(rs.getInt("id"), rs.getString("nom"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la catégorie : " + e.getMessage());
        }
        return null;
    }

    public CategorieCours getOneById(int id) {
        String req = "SELECT * FROM categorie_cours WHERE id=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new CategorieCours(rs.getInt("id"), rs.getString("nom"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la catégorie : " + e.getMessage());
        }
        return null;
    }


}

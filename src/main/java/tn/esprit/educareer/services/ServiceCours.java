package tn.esprit.educareer.services;

import tn.esprit.educareer.interfaces.IService;
import tn.esprit.educareer.models.Cours;
import tn.esprit.educareer.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCours implements IService<Cours> {
    Connection cnx;

    public ServiceCours() {
        cnx = MyConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Cours cours) {
        String req = "Insert Into cours (nom,document,image,requirement,categorie_cours_id,formatteur_id)";
        try {
            PreparedStatement cst = cnx.prepareStatement(req);
            cst.setString(1, cours.getNom());
            cst.setString(2, cours.getDocument());
            cst.setString(3, cours.getImage());
            cst.setString(4, cours.getRequirement());
            cst.setInt(5, cours.getCategorie().getId());
            cst.setInt(6, cours.getUser().getId());

            cst.executeUpdate();
            System.out.println("Cours ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Cours cours) {
        String req = "UPDATE cours SET nom = ?, document = ?, image = ?, requirement = ?, categorie_cours_id = ?, formateur_id = ? WHERE id = ?";
        try {
            PreparedStatement cst = cnx.prepareStatement(req);
            cst.setString(1, cours.getNom());
            cst.setString(2, cours.getDocument());
            cst.setString(3, cours.getImage());
            cst.setString(4, cours.getRequirement());
            cst.setInt(5, cours.getCategorie().getId());
            cst.setInt(6, cours.getUser().getId());
            cst.setInt(7, cours.getId());

            cst.executeUpdate();
            System.out.println("Cours modifié avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Cours cours) {
        String req = "DELETE FROM cours WHERE id = ?";
        try {
            PreparedStatement cst = cnx.prepareStatement(req);
            cst.setInt(1, cours.getId());
            cst.executeUpdate();
            System.out.println("Cours supprimé avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public List<Cours> getAll() {
        List<Cours> coursList = new ArrayList<>();
        String req = "SELECT * FROM cours";
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Cours c = new Cours(rs.getInt("id"), rs.getString("nom"), rs.getString("document"), rs.getString("image"), rs.getString("requirement"), null, // tu peux ici charger la catégorie depuis son ID si besoin
                        null  // idem pour le formateur
                );
                coursList.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des cours : " + e.getMessage());
        }
        return coursList;
    }

    @Override
    public Cours getOne(Cours cours) {
//        String req = "SELECT * FROM cours WHERE id = ?";
//        try {
//            PreparedStatement cst = cnx.prepareStatement(req);
//            cst.setInt(1, cours.getId());
//            ResultSet rs = cst.executeQuery();
//            if (rs.next()) {
//                return new Cours(rs.getInt("id"), rs.getString("nom"), rs.getString("document"), rs.getString("image"), rs.getString("requirement"), null, // charger catégorie si tu veux
//                );
//            }
//        } catch (SQLException e) {
//            System.out.println("Erreur lors de la récupération du cours : " + e.getMessage());
//        }
        return null;
    }
}


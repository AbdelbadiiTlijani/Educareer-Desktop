package tn.esprit.educareer.services;

import tn.esprit.educareer.interfaces.IService;
import tn.esprit.educareer.models.Cours;
import tn.esprit.educareer.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceCours implements IService<Cours> {
    Connection cnx;

    public ServiceCours() {
        cnx = MyConnection.getInstance().getCnx();
    }


    @Override
    public void ajouter(Cours cours) {
        String req = "INSERT INTO `cours`(`nom`, `document`, `image`, `requirement`,`formatteur_id`, `categorie_cours_id`) VALUES (?, ?, ?, ?,?,?)";
        try {
            PreparedStatement cst = cnx.prepareStatement(req);
            cst.setString(1, cours.getNom());
            cst.setString(2, cours.getDocument());
            cst.setString(3, cours.getImage());
            cst.setString(4, cours.getRequirement());
            cst.setInt(5, cours.getUser().getId());
            cst.setInt(6, cours.getCategorie().getId());

            cst.executeUpdate();
            System.out.println("Cours ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Cours cours) {
        String req = "UPDATE `cours` SET `nom`=?,`document`=?,`image`=?,`requirement`=?,`formatteur_id`=?,`categorie_cours_id`=? WHERE `id` = ?";
        try {
            PreparedStatement cst = cnx.prepareStatement(req);
            cst.setString(1, cours.getNom());
            cst.setString(2, cours.getDocument());
            cst.setString(3, cours.getImage());
            cst.setString(4, cours.getRequirement());
            cst.setInt(5, cours.getUser().getId());
            cst.setInt(6, cours.getCategorie().getId());
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
        ServiceCategorieCours serviceCategorieCours = new ServiceCategorieCours();
        ServiceUser serviceUser = new ServiceUser();

        List<Cours> coursList = new ArrayList<>();
        String req = "SELECT * FROM cours";
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Cours c = new Cours(rs.getInt("id"), rs.getString("nom"), rs.getString("requirement"), rs.getString("document"), rs.getString("image"), serviceCategorieCours.getOneById(rs.getInt("categorie_cours_id")), serviceUser.getOneById(rs.getInt("formatteur_id")));
                coursList.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des cours : " + e.getMessage());
        }
        return coursList;
    }


    public Cours getOneById(int id) {
        ServiceCategorieCours serviceCategorieCours = new ServiceCategorieCours();
        ServiceUser serviceUser = new ServiceUser();
        String req = "SELECT * FROM cours WHERE id=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new Cours(rs.getString("nom"), rs.getString("requirement"), rs.getString("document"), rs.getString("image"), serviceCategorieCours.getOneById(rs.getInt("categorie_cours_id")), serviceUser.getOneById(rs.getInt("formatteur_id")));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la catégorie : " + e.getMessage());
        }
        return null;
    }


    @Override
    public Cours getOne(Cours cours) {
        return null;
    }


    public Map<String, Integer> CoursParCategorie() {
        Map<String, Integer> stats = new HashMap<>();
        String req = "SELECT cc.nom AS categorie_nom, COUNT(*) AS nombre_cours FROM cours c JOIN categorie_cours cc ON c.categorie_cours_id = cc.id GROUP BY cc.nom ORDER BY cc.nom";

        try {
            PreparedStatement cst = cnx.prepareStatement(req);
            ResultSet rs = cst.executeQuery();

            while (rs.next()) {
                String nomCategorie = rs.getString("categorie_nom");
                int nombreCours = rs.getInt("nombre_cours");
                stats.put(nomCategorie, nombreCours);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération : " + e.getMessage());
        }
        return stats;
    }


    public Map<String, Integer> formateurParCategorie() {
        Map<String, Integer> stats = new HashMap<>();
        String req = "SELECT cc.nom AS categorie_nom, COUNT(DISTINCT c.formatteur_id) AS nombre_formateurs FROM cours c JOIN categorie_cours cc ON c.categorie_cours_id = cc.id GROUP BY cc.nom ORDER BY cc.nom";

        try {
            PreparedStatement cst = cnx.prepareStatement(req);
            ResultSet rs = cst.executeQuery();

            while (rs.next()) {
                String nomCategorie = rs.getString("categorie_nom");
                int nombreFormatteur = rs.getInt("nombre_formateurs");
                stats.put(nomCategorie, nombreFormatteur);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération : " + e.getMessage());
        }
        return stats;
    }

}



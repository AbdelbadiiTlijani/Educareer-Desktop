package tn.esprit.educareer.services;

import tn.esprit.educareer.models.Projet;
import tn.esprit.educareer.utils.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceProjet {

    private Connection cnx;

    public ServiceProjet() {
        cnx = MyConnection.getInstance().getCnx();
    }

    // Ajouter un projet
    public void ajouter(Projet projet) {
        try {
            String req = "INSERT INTO projet (categorie_id, titre,description, contenu, formateur_id) VALUES (?, ?, ?, ?,?)";
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, projet.getCategorie_id());
            pst.setString(2,projet.getDescription());
            pst.setString(3, projet.getTitre());
            pst.setString(4, projet.getContenu());
            pst.setInt(5, projet.getFormateur_id());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Projet ajouté avec succès !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Modifier un projet
    public void modifier(Projet projet) {
        try {
            String req = "UPDATE projet SET categorie_id = ?, titre = ?, contenu = ? WHERE id = ?";
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, projet.getCategorie_id());
            pst.setString(2, projet.getTitre());
            pst.setString(3, projet.getContenu());
            pst.setInt(4, projet.getId());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Projet modifié avec succès !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprimer un projet
    public void supprimer(Projet projet) {
        try {
            String req = "DELETE FROM projet WHERE id = ?";
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, projet.getId());
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Projet supprimé avec succès !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lire tous les projets avec jointure sur la table categorie_projet
    public List<Projet> getAll() {
        List<Projet> projets = new ArrayList<>();
        try {
            String req = "SELECT p.id, p.categorie_id, p.titre, p.description, p.contenu, p.formateur_id, c.categorie " +
                    "FROM projet p " +
                    "JOIN categorie_projet c ON p.categorie_id = c.id";
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);

            while (rs.next()) {
                Projet projet = new Projet(
                        rs.getInt("id"),
                        rs.getInt("categorie_id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("contenu"),
                        rs.getInt("formateur_id")
                );
                projet.setCategorieNom(rs.getString("categorie")); // Remplir le nom de la catégorie
                projets.add(projet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projets;
    }


}

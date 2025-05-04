package tn.esprit.educareer.services;

import tn.esprit.educareer.models.CategorieProjet;
import tn.esprit.educareer.models.Projet;
import tn.esprit.educareer.utils.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import tn.esprit.educareer.models.User;

public class ServiceProjet {

    private static Connection cnx;

    public ServiceProjet() {
        cnx = MyConnection.getInstance().getCnx();
    }

    // Ajouter un projet
    public void ajouter(Projet projet) {
        try {
            String req = "INSERT INTO projet (categorie_id, titre,description, contenu, formateur_id) VALUES (?, ?, ?, ?,?)";
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, projet.getCategorie_id());
            pst.setString(2,projet.getTitre());
            pst.setString(3, projet.getDescription());
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
            String req = "UPDATE projet SET categorie_id = ?, titre = ?, contenu = ? , description=? WHERE id = ?";
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, projet.getCategorie_id());
            pst.setString(2, projet.getTitre());
            pst.setString(3, projet.getContenu());
            pst.setString(4, projet.getDescription());
            pst.setInt(5, projet.getId());

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

    // Récupérer un formateur par ID avec tous ses détails
    public User getFormateurDetailsById(int formateurId) {
        User formateur = null;
        try {
            String req = "SELECT id, nom, prenom, email, role FROM user WHERE id = ?";
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, formateurId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                formateur = new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        "",  // L'email est récupéré, donc mot de passe non requis ici
                        "",  // Photo de profil
                        rs.getString("role"),
                        "",  // Token de vérification
                        "",  // Date d'inscription
                        ""   // Date (si besoin)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return formateur;
    }


    // Dans ServiceProjet.java

    // Méthode pour récupérer le nombre de participants d'un projet
    public static int getNombreParticipants(int projetId) {
        int nbParticipants = 0;
        try {
            String req = "SELECT COUNT(*) FROM participation_projet WHERE projet_id = ?";
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, projetId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                nbParticipants = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nbParticipants;
    }


    public static CategorieProjet getCategorieById(int categorieId) {
        CategorieProjet categorieProjet = null;
        try {
            Connection cnx = MyConnection.getInstance().getCnx(); // make sure you have connection here
            String req = "SELECT * FROM categorie_projet WHERE id = ?";
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, categorieId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String categorieName = rs.getString("categorie");
                int isValidated = rs.getInt("is_validated");
                int isDeleted = rs.getInt("is_deleted");

                categorieProjet = new CategorieProjet(id, categorieName, isValidated, isDeleted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorieProjet;
    }

    public List<Projet> getAllByFormateur(int idForma) {
        List<Projet> projets = new ArrayList<>();
        try {
            String req = "SELECT p.id, p.categorie_id, p.titre, p.description, p.contenu, p.formateur_id, c.categorie " +
                    "FROM projet p " +
                    "JOIN categorie_projet c ON p.categorie_id = c.id " +
                    "WHERE p.formateur_id = ?";
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, idForma);
            ResultSet rs = pst.executeQuery();

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

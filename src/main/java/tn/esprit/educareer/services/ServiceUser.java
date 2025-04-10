package tn.esprit.educareer.services;
import org.mindrot.jbcrypt.BCrypt;


import tn.esprit.educareer.interfaces.IService;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.utils.MyConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser implements IService<User> {
    Connection cnx ;
    public ServiceUser(){
        cnx= MyConnection.getInstance().getCnx();
    }
    private String hashPassword(String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(13));
        // Replace $2a$ with $2y$ for Symfony compatibility
        if (hashed.startsWith("$2a$")) {
            hashed = "$2y$" + hashed.substring(4);
        }
        return hashed;
    }

    @Override
    public void ajouter(User user) {
        String hashedPassword = hashPassword(user.getMdp());
        String req = "INSERT INTO user (status, nom, prenom, email, mdp, photo_profil, role, verification_token, date_inscription, date) " +
                "VALUES (" + user.getStatus() + ", '" + user.getNom() + "', '" + user.getPrenom() + "', '" +
                user.getEmail() + "', '" + hashedPassword + "', '" + user.getPhoto_profil() + "', '" +
                user.getRole() + "', '" + user.getVerification_token() + "', '" + user.getdate_inscription() +
                "', '" + user.getDate() + "')";

        try {
            Statement st = cnx.createStatement();
            st.executeUpdate(req);
            System.out.println("User ajoutée");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout : " + e.getMessage());
        }

    }

    @Override
    public void modifier(User user) {
        try {
            Statement st = cnx.createStatement();
            String req = "UPDATE user SET "
                    + "status = " + user.getStatus() + ", "
                    + "nom = '" + user.getNom() + "', "
                    + "prenom = '" + user.getPrenom() + "', "
                    + "email = '" + user.getEmail() + "', "
                    + "mdp = '" + user.getMdp() + "', "
                    + "photo_profil = '" + user.getPhoto_profil() + "', "
                    + "role = '" + user.getRole() + "', "
                    + "verification_token = '" + user.getVerification_token() + "', "
                    + "date_inscription = '" + user.getdate_inscription() + "', "
                    + "date = '" + user.getDate() + "' "
                    + "WHERE id = " + user.getId();
            int rowsAffected = st.executeUpdate(req);

            if (rowsAffected > 0) {
                System.out.println("Utilisateur mis à jour avec succès !");
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(User user) {
        String req = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, user.getId());
            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Utilisateur '" + user.getNom() + "' (ID: " + user.getId() + ") supprimé avec succès.");
            } else {
                System.out.println("Aucun utilisateur trouvé avec l'ID: " + user.getId());
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }


    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try {
            String req = "SELECT * FROM user"; // Assure-toi que la table s'appelle bien 'user'
            PreparedStatement pst = cnx.prepareStatement(req);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                User us = new User(
                        rs.getInt("id"),
                        rs.getInt("status"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mdp"),
                        rs.getString("photo_profil"),
                        rs.getString("role"),
                        rs.getString("verification_token"),
                        rs.getString("date_inscription"),
                        rs.getString("date")
                );
                users.add(us);
                System.out.println("User récupéré : " + us);
            }

            System.out.println("Nombre total d'utilisateurs : " + users.size());

        } catch (SQLException ex) {
            System.out.println("Erreur lors de la récupération des utilisateurs : " + ex.getMessage());
        }
        return users;
    }


    @Override
    public User getOne(User user) {
        return null;
    }
}

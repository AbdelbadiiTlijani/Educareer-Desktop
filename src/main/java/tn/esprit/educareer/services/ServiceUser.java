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
    Connection cnx;
    public ServiceUser(){
        cnx = MyConnection.getInstance().getCnx();
    }

    private String hashPassword(String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(13));
        // Replace $2a$ with $2y$ for Symfony compatibility
        if (hashed.startsWith("$2a$")) {
            hashed = "$2y$" + hashed.substring(4);
        }
        return hashed;
    }
    public void approuverUser(User user) {
        String sql = "UPDATE user SET status = 1 WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                user.setStatus(1); // Update local object
                System.out.println("Utilisateur approuvé avec succès !");
            } else {
                System.out.println("Aucun utilisateur trouvé avec l'ID: " + user.getId());
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'approbation : " + e.getMessage());
        }
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
            // Check if password has changed
            String currentPassword = user.getMdp();
            boolean passwordChanged = !currentPassword.startsWith("$2y$");

            // Prepare the SQL statement sans date_inscription
            String sql = "UPDATE user SET " +

                    "nom = ?, " +
                    "prenom = ?, " +

                    "mdp = ?, " +
                    "photo_profil = ? " +

                    "WHERE id = ?";

            PreparedStatement pst = cnx.prepareStatement(sql);

            // Set parameters (date_inscription exclue)

            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setString(3, passwordChanged ? hashPassword(currentPassword) : currentPassword);
            pst.setString(4, user.getPhoto_profil());
            pst.setInt(5, user.getId());

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Utilisateur mis à jour avec succès !");
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
            e.printStackTrace();
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
            String req = "SELECT * FROM user";
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

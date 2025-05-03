package tn.esprit.educareer.services;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.mindrot.jbcrypt.BCrypt;

import tn.esprit.educareer.interfaces.IService;
import tn.esprit.educareer.models.Type_Offre;
import tn.esprit.educareer.utils.MyConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeOffreService implements IService<Type_Offre> {

    private Connection cnx;
    public TypeOffreService() {
        cnx = MyConnection.getInstance().getCnx(); // même style que ServiceUser
    }

    @Override
    public void ajouter(Type_Offre typeOffre) {
        // Vérification que la catégorie n'est pas vide et contient au moins 2 caractères
        if (typeOffre.getCategorie() == null || typeOffre.getCategorie().trim().isEmpty() || typeOffre.getCategorie().trim().length() < 2) {
            showAlert("Erreur", "La catégorie doit contenir au moins 2 caractères et ne peut pas être vide.");
            return;
        }

        String req = "INSERT INTO type_offre (categorie) VALUES (?)";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, typeOffre.getCategorie());
            pst.executeUpdate();
            System.out.println("Type d'offre ajouté");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    @Override
    public void modifier(Type_Offre typeOffre) {
        // Vérification que la catégorie n'est pas vide et contient au moins 2 caractères
        if (typeOffre.getCategorie() == null || typeOffre.getCategorie().trim().isEmpty() || typeOffre.getCategorie().trim().length() < 2) {
            showAlert("Erreur", "La catégorie doit contenir au moins 2 caractères et ne peut pas être vide.");
            return;
        }

        String req = "UPDATE type_offre SET categorie=? WHERE id=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, typeOffre.getCategorie());
            pst.setInt(2, typeOffre.getId());
            pst.executeUpdate();
            System.out.println("Type d'offre modifié");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void supprimer(Type_Offre typeOffre) {
        String req = "DELETE FROM type_offre WHERE id=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, typeOffre.getId());
            pst.executeUpdate();
            System.out.println("Type d'offre supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Type_Offre> getAll() {
        List<Type_Offre> types = new ArrayList<>();
        try {
            String req = "SELECT * FROM type_offre";
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);

            while (rs.next()) {
                Type_Offre type = new Type_Offre(rs.getInt("id"), rs.getString("categorie"));
                types.add(type);
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
        return types; // Jamais null
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null); // Pas de texte d'en-tête
        alert.showAndWait(); // Affiche l'alerte et attend l'interaction de l'utilisateur
    }
    @Override
    public Type_Offre getOne(Type_Offre typeOffre) {
        return null;
    }

    /*@Override
    public List<Type_Offre> rechercher() {
        List<Type_Offre> types = new ArrayList<>();
        String req = "SELECT * FROM type_offre";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Type_Offre type = new Type_Offre(rs.getInt("id"), rs.getString("categorie"));
                types.add(type);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return types;
    }

     */
}


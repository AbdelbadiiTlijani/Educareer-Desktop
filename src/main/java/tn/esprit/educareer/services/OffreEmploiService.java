package tn.esprit.educareer.services;
import org.mindrot.jbcrypt.BCrypt;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import tn.esprit.educareer.interfaces.IService;
import tn.esprit.educareer.models.Offre_Emploi;
import tn.esprit.educareer.utils.MyConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class OffreEmploiService implements IService<Offre_Emploi> {

    private Connection cnx;

    public OffreEmploiService() {
        cnx = MyConnection.getInstance().getCnx(); // même style que ServiceUser
    }

    @Override
    public void ajouter(Offre_Emploi offre) {
        // Contrôle de saisie et affichage d'alertes en cas d'erreur
        if (offre.getTitre().isEmpty() || offre.getTitre().length() < 3) {
            showAlert("Erreur", "Le titre doit contenir au moins 3 caractères et ne peut pas être vide.");
            return;
        }

        if (offre.getLieu().isEmpty() || offre.getLieu().length() < 3) {
            showAlert("Erreur", "Le lieu doit contenir au moins 3 caractères et ne peut pas être vide.");
            return;
        }

        if (offre.getDescoffre().isEmpty() || offre.getDescoffre().length() < 3) {
            showAlert("Erreur", "La description de l'offre doit contenir au moins 3 caractères et ne peut pas être vide.");
            return;
        }

        if (offre.getSalaire() <= 0) {
            showAlert("Erreur", "Le salaire doit être un nombre positif.");
            return;
        }

        // Si tous les contrôles sont OK, on procède à l'ajout
        String req = "INSERT INTO offre_emploi (titre, descoffre, lieu, salaire, type_offre_id) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, offre.getTitre());
            pst.setString(2, offre.getDescoffre());
            pst.setString(3, offre.getLieu());
            pst.setDouble(4, offre.getSalaire());
            pst.setInt(5, offre.getTypeOffre().getId());  // Assurez-vous de stocker l'ID du type d'offre
            pst.executeUpdate();
            System.out.println("Offre ajoutée ");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Méthode pour modifier une offre existante
    public void modifier(Offre_Emploi offre) {
        // Contrôle de saisie et affichage d'alertes en cas d'erreur
        if (offre.getTitre().isEmpty() || offre.getTitre().length() < 3) {
            showAlert("Erreur", "Le titre doit contenir au moins 3 caractères et ne peut pas être vide.");
            return;
        }

        if (offre.getLieu().isEmpty() || offre.getLieu().length() < 3) {
            showAlert("Erreur", "Le lieu doit contenir au moins 3 caractères et ne peut pas être vide.");
            return;
        }

        if (offre.getDescoffre().isEmpty() || offre.getDescoffre().length() < 3) {
            showAlert("Erreur", "La description de l'offre doit contenir au moins 3 caractères et ne peut pas être vide.");
            return;
        }

        if (offre.getSalaire() <= 0) {
            showAlert("Erreur", "Le salaire doit être un nombre positif.");
            return;
        }

        // Si tous les contrôles sont OK, on procède à la mise à jour
        String req = "UPDATE offre_emploi SET titre=?, descoffre=?, lieu=?, salaire=?, type_offre_id=? WHERE id=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, offre.getTitre());
            pst.setString(2, offre.getDescoffre());
            pst.setString(3, offre.getLieu());
            pst.setDouble(4, offre.getSalaire());
            pst.setInt(5, offre.getTypeOffre().getId());  // Assurez-vous de stocker l'ID du type d'offre
            pst.setInt(6, offre.getId());  // L'ID de l'offre à modifier
            pst.executeUpdate();
            System.out.println("Offre modifiée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null); // Pas de texte d'en-tête
        alert.showAndWait(); // Affiche l'alerte et attend l'interaction de l'utilisateur
    }

    @Override
    public void supprimer(Offre_Emploi offre) {
        String req = "DELETE FROM offre_emploi WHERE id=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, offre.getId());
            pst.executeUpdate();
            System.out.println("Offre supprimée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }




    @Override
    public Offre_Emploi getOne(Offre_Emploi offreEmploi) {
        return null;
    }

    @Override
    public List<Offre_Emploi> getAll() {
        List<Offre_Emploi> offres = new ArrayList<>();
        try {
            String req = "SELECT * FROM offre_emploi";
            PreparedStatement pst = cnx.prepareStatement(req);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Offre_Emploi offre = new Offre_Emploi(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("descoffre"),
                        rs.getString("lieu"),
                        rs.getDouble("salaire")
                );
                offres.add(offre);
            }

            System.out.println("Nombre total d'offres d'emploi : " + offres.size());

        } catch (SQLException ex) {
            System.out.println("Erreur lors de la récupération des offres d'emploi : " + ex.getMessage());
        }
        return offres;
    }


}

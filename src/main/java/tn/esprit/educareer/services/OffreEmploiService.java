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
        cnx = MyConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Offre_Emploi offre) {
        // Vérification si un seul champ est vide ou invalide
        if (offre.getTitre() == null || offre.getTitre().trim().isEmpty() ||
                offre.getDescoffre() == null || offre.getDescoffre().trim().isEmpty() ||
                offre.getLieu() == null || offre.getLieu().trim().isEmpty() ||
                offre.getSalaire() <= 0 ||
                offre.getTypeOffre() == null) {

            showAlert("Erreur", "Tous les champs doivent être remplis correctement !");
            return;
        }

        // Vérification spécifique sur la longueur minimale de titre, descoffre et lieu
        if (offre.getTitre().trim().length() < 2) {
            showAlert("Erreur", "Le titre doit contenir au moins 2 caractères.");
            return;
        }

        if (offre.getDescoffre().trim().length() < 2) {
            showAlert("Erreur", "La description doit contenir au moins 2 caractères.");
            return;
        }

        if (offre.getLieu().trim().length() < 2) {
            showAlert("Erreur", "Le lieu doit contenir au moins 2 caractères.");
            return;
        }

        // Si tout est correct, insérer dans la base
        String req = "INSERT INTO offre_emploi (titre, descoffre, lieu, salaire, type_offre_id) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, offre.getTitre().trim());
            pst.setString(2, offre.getDescoffre().trim());
            pst.setString(3, offre.getLieu().trim());
            pst.setDouble(4, offre.getSalaire());
            pst.setInt(5, offre.getTypeOffre().getId());
            pst.executeUpdate();
            System.out.println("Offre ajoutée avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }



    // Méthode pour modifier une offre existante
    public void modifier(Offre_Emploi offre) {
        // Vérification si un seul champ est vide ou invalide
        if (offre.getTitre() == null || offre.getTitre().trim().isEmpty() ||
                offre.getDescoffre() == null || offre.getDescoffre().trim().isEmpty() ||
                offre.getLieu() == null || offre.getLieu().trim().isEmpty() ||
                offre.getSalaire() <= 0 ||
                offre.getTypeOffre() == null) {

            showAlert("Erreur", "Tous les champs doivent être remplis correctement !");
            return;
        }

        // Vérification spécifique sur la longueur minimale de titre, descoffre et lieu
        if (offre.getTitre().trim().length() < 2) {
            showAlert("Erreur", "Le titre doit contenir au moins 2 caractères.");
            return;
        }

        if (offre.getDescoffre().trim().length() < 2) {
            showAlert("Erreur", "La description doit contenir au moins 2 caractères.");
            return;
        }

        if (offre.getLieu().trim().length() < 2) {
            showAlert("Erreur", "Le lieu doit contenir au moins 2 caractères.");
            return;
        }

        // Si tout est correct, mettre à jour l'offre dans la base de données
        String req = "UPDATE offre_emploi SET titre=?, descoffre=?, lieu=?, salaire=?, type_offre_id=? WHERE id=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, offre.getTitre().trim());
            pst.setString(2, offre.getDescoffre().trim());
            pst.setString(3, offre.getLieu().trim());
            pst.setDouble(4, offre.getSalaire());
            pst.setInt(5, offre.getTypeOffre().getId());  // Assurez-vous de stocker l'ID du type d'offre
            pst.setInt(6, offre.getId());  // L'ID de l'offre à modifier
            pst.executeUpdate();
            System.out.println("Offre modifiée avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
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
                int nombrePostulations = rs.getInt("nombre_postulations");
                double salaire = rs.getDouble("salaire");

                // Si le nombre de postulations est inférieur à 3, augmenter le salaire de 20%
                if (nombrePostulations < 3) {
                    salaire *= 1.20;  // Augmenter le salaire de 20%

                    // Mise à jour de la base de données pour refléter l'augmentation du salaire
                    String updateReq = "UPDATE offre_emploi SET salaire = ? WHERE id = ?";
                    PreparedStatement pstUpdate = cnx.prepareStatement(updateReq);
                    pstUpdate.setDouble(1, salaire);
                    pstUpdate.setInt(2, rs.getInt("id"));
                    pstUpdate.executeUpdate();
                }

                // Crée l'objet Offre_Emploi avec le salaire mis à jour
                Offre_Emploi offre = new Offre_Emploi(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("descoffre"),
                        rs.getString("lieu"),
                        salaire,  // Salaire mis à jour
                        nombrePostulations
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

package tn.esprit.educareer.services;
import okhttp3.*;
import java.util.Collections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import okhttp3.*;
import tn.esprit.educareer.interfaces.IService;
import tn.esprit.educareer.models.Offre_Emploi;
import tn.esprit.educareer.models.Postulation;
import tn.esprit.educareer.utils.MyConnection;

import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class PostulationService implements IService<Postulation> {

    private Connection cnx;

    public PostulationService() {
        cnx = MyConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Postulation postulation) {
        if (postulation.getNom() == null || postulation.getNom().trim().isEmpty() ||
                postulation.getPrenom() == null || postulation.getPrenom().trim().isEmpty() ||
                postulation.getResume() == null || postulation.getResume().trim().isEmpty() ||
                postulation.getOffre() == null) {

            showAlert("Erreur", "Tous les champs doivent être remplis correctement !");
            return;
        }

        if (postulation.getNom().trim().length() < 2) {
            showAlert("Erreur", "Le nom doit contenir au moins 2 caractères.");
            return;
        }

        if (postulation.getPrenom().trim().length() < 2) {
            showAlert("Erreur", "Le prénom doit contenir au moins 2 caractères.");
            return;
        }

        String req = "INSERT INTO postulation (nom, prenom, resume, offre_id) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, postulation.getNom().trim());
            pst.setString(2, postulation.getPrenom().trim());
            pst.setString(3, postulation.getResume().trim());
            pst.setInt(4, postulation.getOffre().getId());
            pst.executeUpdate();

            System.out.println("Postulation ajoutée avec succès.");

            String updateReq = "UPDATE offre_emploi SET nombre_postulations = nombre_postulations + 1 WHERE id = ?";
            PreparedStatement pstUpdate = cnx.prepareStatement(updateReq);
            pstUpdate.setInt(1, postulation.getOffre().getId());
            pstUpdate.executeUpdate();

            System.out.println("Nombre de postulations mis à jour avec succès.");

            //vibr
            envoyerViberMessage(postulation.getNom(), postulation.getPrenom());

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }

    private void envoyerViberMessage(String nom, String prenom) {
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1)) // 💥 Forcer HTTP/1.1 ici
                .build();

        MediaType mediaType = MediaType.parse("application/json");

        // msg vbr
        String jsonBody = "{"
                + "\"messages\":[{"
                + "\"sender\":\"DemoCompany\","
                + "\"destinations\":[{\"to\":\"21658721759\"}],"
                + "\"content\":{"
                + "\"text\":\"Bonjour " + nom + " " + prenom + ",\\n\\nVotre postulation a été enregistrée avec succès sur Educareer! 🚀\","
                + "\"type\":\"TEXT\""
                + "}"
                + "}]"
                + "}";

        RequestBody body = RequestBody.create(mediaType, jsonBody);

        Request request = new Request.Builder()
                .url("https://pevmm3.api.infobip.com/viber/2/messages")
                .post(body)
                .addHeader("Authorization", "App 7734670fae130a610987d62a22f5b752-0dd49df9-2c12-4358-91b3-d3454ba02161") // 🔥 Mets ta clé API ici
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Message Viber envoyé avec succès.");
            } else {
                System.out.println("Erreur lors de l'envoi du message Viber : " + response.code() + " " + response.message());
            }
        } catch (Exception e) {
            System.out.println("Exception lors de l'envoi du message Viber : " + e.getMessage());
        }
    }


    @Override
    public void modifier(Postulation postulation) {
        if (postulation.getNom() == null || postulation.getNom().trim().isEmpty() ||
                postulation.getPrenom() == null || postulation.getPrenom().trim().isEmpty() ||
                postulation.getResume() == null || postulation.getResume().trim().isEmpty() ||
                postulation.getOffre() == null) {

            showAlert("Erreur", "Tous les champs doivent être remplis correctement !");
            return;
        }

        if (postulation.getNom().trim().length() < 2) {
            showAlert("Erreur", "Le nom doit contenir au moins 2 caractères.");
            return;
        }

        if (postulation.getPrenom().trim().length() < 2) {
            showAlert("Erreur", "Le prénom doit contenir au moins 2 caractères.");
            return;
        }

        String req = "UPDATE postulation SET nom=?, prenom=?, resume=?, offre_id=? WHERE id=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, postulation.getNom().trim());
            pst.setString(2, postulation.getPrenom().trim());
            pst.setString(3, postulation.getResume().trim());
            pst.setInt(4, postulation.getOffre().getId());
            pst.setInt(5, postulation.getId());
            pst.executeUpdate();
            System.out.println("Postulation modifiée avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }

    private void mettreAJourSalaire(int offreId) {
        try {
            // Récupérer le nombre de postulations et le salaire actuel pour cette offre
            String query = "SELECT nombre_postulations, salaire FROM offre_emploi WHERE id = ?";
            PreparedStatement pst = cnx.prepareStatement(query);
            pst.setInt(1, offreId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int nombrePostulations = rs.getInt("nombre_postulations");
                double salaire = rs.getDouble("salaire");

                // Si le nombre de postulations est inférieur à 3, augmenter le salaire de 20%
                if (nombrePostulations < 3) {
                    double nouveauSalaire = salaire * 1.20;  // Augmenter le salaire de 20%

                    // Mettre à jour le salaire dans la base de données
                    String updateQuery = "UPDATE offre_emploi SET salaire = ? WHERE id = ?";
                    PreparedStatement updatePst = cnx.prepareStatement(updateQuery);
                    updatePst.setDouble(1, nouveauSalaire);
                    updatePst.setInt(2, offreId);
                    updatePst.executeUpdate();
                    System.out.println("Salaire mis à jour à " + nouveauSalaire);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du salaire : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Postulation postulation) {
        String req = "DELETE FROM postulation WHERE id=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, postulation.getId());
            pst.executeUpdate();
            System.out.println("Postulation supprimée.");
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }

    @Override
    public Postulation getOne(Postulation postulation) {
        return null; // Pas utilisé pour l'instant
    }

    @Override
    public List<Postulation> getAll() {
        List<Postulation> postulations = new ArrayList<>();
        try {
            // Requête SQL qui joint les postulations avec les titres des offres
            String req = "SELECT p.*, o.titre FROM postulation p JOIN offre_emploi o ON p.offre_id = o.id";
            PreparedStatement pst = cnx.prepareStatement(req);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                // Crée l'objet Offre_Emploi avec juste le titre de l'offre
                Offre_Emploi offre = new Offre_Emploi(
                        rs.getInt("offre_id"), // ID de l'offre, mais on ne le stocke pas ici
                        rs.getString("titre"), // Titre de l'offre
                        "", "", 0.0            // Autres attributs vides car non nécessaires ici
                );

                // Crée l'objet Postulation avec les données de la base
                Postulation p = new Postulation(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("resume")
                );
                // Associe l'offre à la postulation
                p.setOffre(offre);

                // Ajoute la postulation à la liste
                postulations.add(p);
            }

            System.out.println("Nombre total de postulations : " + postulations.size());

        } catch (SQLException ex) {
            System.out.println("Erreur lors de la récupération des postulations : " + ex.getMessage());
        }
        return postulations;
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

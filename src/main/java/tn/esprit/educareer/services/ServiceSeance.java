package tn.esprit.educareer.services;

import tn.esprit.educareer.interfaces.IService;
import tn.esprit.educareer.models.Seance;
import tn.esprit.educareer.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceSeance implements IService<Seance> {
    Connection cnx;

    public ServiceSeance() {
        cnx = MyConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Seance seance) {
        String req = "INSERT INTO seance (titre, description, date_heure, duree,cours_id) VALUES (?, ?, ?, ?,?)";
        try {
            PreparedStatement cst = cnx.prepareStatement(req);
            cst.setString(1, seance.getTitre());
            cst.setString(2, seance.getDescription());
            cst.setTimestamp(3, new Timestamp(seance.getDate_heure().getTime()));
            cst.setInt(4, seance.getDuree());
            cst.setInt(5, seance.getCours().getId());

            cst.executeUpdate();
            System.out.println("Séance ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Seance seance) {
        String req = "update seance set titre=?, description=?, date_heure=?, duree=?,cours_id=? where id=?";
        try {
            PreparedStatement cst = cnx.prepareStatement(req);
            cst.setString(1, seance.getTitre());
            cst.setString(2, seance.getDescription());
            cst.setTimestamp(3, new Timestamp(seance.getDate_heure().getTime()));
            cst.setInt(4, seance.getDuree());
            cst.setInt(5, seance.getCours().getId());
            cst.setInt(6, seance.getId());

            cst.executeUpdate();
            System.out.println("Séance modifiée avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(Seance seance) {
        String req = "delete from seance where id=?";
        try {
            PreparedStatement cst = cnx.prepareStatement(req);
            cst.setInt(1, seance.getId());
            cst.executeUpdate();
            System.out.println("seance supprimé avec succés");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Seance> getAll() {
        ServiceCours serviceCours = new ServiceCours();
        List<Seance> list = new ArrayList<>();
        String req = "SELECT * FROM seance";
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Seance s = new Seance(rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        new Date(rs.getTimestamp("date_heure").getTime()),
                        rs.getInt("duree"),
                        serviceCours.getOneById(rs.getInt("cours_id"))
                );
                list.add(s);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    @Override
    public Seance getOne(Seance seance) {
        return null;
    }
}

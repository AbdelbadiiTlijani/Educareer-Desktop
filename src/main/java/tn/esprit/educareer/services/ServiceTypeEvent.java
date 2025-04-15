package tn.esprit.educareer.services;

import tn.esprit.educareer.interfaces.IService;
import tn.esprit.educareer.models.TypeEvent;
import tn.esprit.educareer.utils.MyConnection;


import java.sql.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class ServiceTypeEvent implements IService<TypeEvent> {

    Connection cnx;

    public ServiceTypeEvent() {
        cnx = MyConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(TypeEvent typeEvent) {
        String req = "INSERT INTO type_event (nom_e) VALUES ('" + typeEvent.getNomE() + "')";
        try {
            Statement st = cnx.createStatement();
            st.executeUpdate(req);
            System.out.println(" Type d'événement ajouté !");
        } catch (SQLException e) {
            System.out.println(" Erreur d'ajout : " + e.getMessage());
        }
    }

    @Override
    public void modifier(TypeEvent typeEvent) {
        String req = "UPDATE type_event SET nom_e = '" + typeEvent.getNomE() + "' WHERE id = " + typeEvent.getId();
        try {
            Statement st = cnx.createStatement();
            st.executeUpdate(req);
            System.out.println(" Type d'événement modifié !");
        } catch (SQLException e) {
            System.out.println("Erreur de modification : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(TypeEvent typeEvent) {
        String req = "DELETE FROM type_event WHERE id = " + typeEvent.getId();
        try {
            Statement st = cnx.createStatement();
            st.executeUpdate(req);
            System.out.println(" Type d'événement supprimé !");
        } catch (SQLException e) {
            System.out.println(" Erreur de suppression : " + e.getMessage());
        }
    }

    @Override
    public List<TypeEvent> getAll() {
        List<TypeEvent> types = new ArrayList<>();
        String req = "SELECT * FROM type_event";

        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                TypeEvent te = new TypeEvent(rs.getInt("id"), rs.getString("nom_e"));
                types.add(te);
            }
        } catch (SQLException e) {
            System.out.println(" Erreur de récupération : " + e.getMessage());
        }

        return types;
    }

    @Override
    public TypeEvent getOne(TypeEvent typeEvent) {
        String req = "SELECT * FROM type_event WHERE id = " + typeEvent.getId();
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            if (rs.next()) {
                return new TypeEvent(rs.getInt("id"), rs.getString("nom_e"));
            }
        } catch (SQLException e) {
            System.out.println(" Erreur lors de la récupération d’un type : " + e.getMessage());
        }

        return null; // Si non trouvé
    }
}

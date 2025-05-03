package tn.esprit.educareer.models;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CategorieCours {
    private int id;
    private String nom;
    private Timestamp  dateCreation;
    private List<Cours> coursList = new ArrayList<>();


    public CategorieCours() {}

    public CategorieCours(int id, String nom, Timestamp dateCreation) {
        this.id = id;
        this.nom = nom;
        this.dateCreation = dateCreation;
    }

    public CategorieCours(String nom) {
        this.nom = nom;
        this.dateCreation = new Timestamp(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<Cours> getCoursList() {
        return coursList;
    }

    public void setCoursList(List<Cours> coursList) {
        this.coursList = coursList;
    }


    @Override
    public String toString() {
        return "CategorieCours{" + "id=" + id + ", nom='" + nom + '\'' + '}';
    }
}

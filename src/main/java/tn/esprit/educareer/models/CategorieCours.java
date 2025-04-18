package tn.esprit.educareer.models;


import java.util.ArrayList;
import java.util.List;

public class CategorieCours {
    private int id;
    private String nom;
    private List<Cours> coursList = new ArrayList<>();


    public CategorieCours() {}

    public CategorieCours(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public CategorieCours(String nom) {
        this.nom = nom;
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

    public List<Cours> getCoursList() {
        return coursList;
    }

    public void setCoursList(List<Cours> coursList) {
        this.coursList = coursList;
    }

    public void addCours(Cours cours){
        coursList.add(cours);
        cours.setCategorie(this);
    }


    @Override
    public String toString() {
        return "CategorieCours{" + "id=" + id + ", nom='" + nom + '\'' + '}';
    }
}

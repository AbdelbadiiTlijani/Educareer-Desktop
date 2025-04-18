package tn.esprit.educareer.models;

import java.util.ArrayList;
import java.util.List;

public class Cours {
    private int id;
    private String nom;
    private String document;
    private String image;
    private String requirement;
    private CategorieCours categorie;
    private User user;
    private List<Seance> seanceList = new ArrayList<>();


    public Cours(String nom, String document, String image, String requirement, CategorieCours categorie , User user) {
        this.nom = nom;
        this.document = document;
        this.image = image;
        this.requirement = requirement;
        this.categorie = categorie;
        this.user = user;
    }

    public Cours(int id, String nom, String document, String image, String requirement, CategorieCours categorie , User user) {
        this.id = id;
        this.nom = nom;
        this.document = document;
        this.image = image;
        this.requirement = requirement;
        this.categorie = categorie;
        this.user = user;
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

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public CategorieCours getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieCours categorie) {
        this.categorie = categorie;
    }

    public List<Seance> getSeanceList() {
        return seanceList;
    }

    public void setSeanceList(List<Seance> seanceList) {
        this.seanceList = seanceList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Cours{" + "id=" + id + ", nom='" + nom + '\'' + ", document='" + document + '\'' + ", image='" + image + '\'' + ", requirement='" + requirement + '\'' + ", categorie=" + categorie + ", seanceList=" + seanceList + '}';
    }
}
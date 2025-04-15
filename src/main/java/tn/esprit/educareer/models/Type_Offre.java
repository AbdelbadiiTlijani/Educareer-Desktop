package tn.esprit.educareer.models;

public class Type_Offre {
    private int id;
    private String categorie;

    public Type_Offre() {}

    public Type_Offre(int id, String categorie) {
        this.id = id;
        this.categorie = categorie;
    }

    public Type_Offre(String categorie) {
        this.categorie = categorie;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    @Override
    public String toString() {
        return categorie;
    }
}

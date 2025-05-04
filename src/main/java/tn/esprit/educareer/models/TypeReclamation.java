package tn.esprit.educareer.models;

public class TypeReclamation {
    private int id;
    private String nom;
    private String description;

    // Constructeur vide
    public TypeReclamation() {
    }

    // Constructeur complet
    public TypeReclamation(int id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    // Constructeur sans ID (utile pour les insertions)
    public TypeReclamation(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    // Getters et Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Très utile pour affichage dans les ComboBox
    @Override
    public String toString() {
        return nom;
    }
}

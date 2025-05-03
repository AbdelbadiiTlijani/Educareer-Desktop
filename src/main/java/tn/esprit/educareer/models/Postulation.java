package tn.esprit.educareer.models;

public class Postulation {
    private int id;
    private String nom;
    private String prenom;
    private String resume;
    private Offre_Emploi offre; // Référence à l'objet Offre_Emploi

    // Constructeur avec Offre_Emploi
    public Postulation(String nom, String prenom, String resume, Offre_Emploi offre) {
        this.nom = nom;
        this.prenom = prenom;
        this.resume = resume;
        this.offre = offre;
    }

    // Constructeur sans Offre_Emploi pour initialisation manuelle
    public Postulation(int id, String nom, String prenom, String resume) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.resume = resume;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Offre_Emploi getOffre() {
        return offre;
    }

    public void setOffre(Offre_Emploi offre) {
        this.offre = offre;
    }

    @Override
    public String toString() {
        return nom + " " + prenom + " - " + (offre != null ? offre.getTitre() : "Pas d'offre associée");
    }
}

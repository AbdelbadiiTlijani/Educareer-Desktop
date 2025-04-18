package tn.esprit.educareer.models;

public class Offre_Emploi {
    private int id;

    private String titre;
    private String descoffre;
    private String lieu;
    private double salaire;
    private Type_Offre typeOffre;  // Référence à l'objet Type_Offre

    // Constructeur avec Type_Offre
    public Offre_Emploi(String titre, String descoffre, String lieu, double salaire, Type_Offre typeOffre) {
        this.titre = titre;
        this.descoffre = descoffre;
        this.lieu = lieu;
        this.salaire = salaire;
        this.typeOffre = typeOffre;

    }

    // Constructeur sans Type_Offre pour initialisation manuelle
    public Offre_Emploi(int id, String titre, String descoffre, String lieu, double salaire) {
        this.id = id;
        this.titre = titre;
        this.descoffre = descoffre;
        this.lieu = lieu;
        this.salaire = salaire;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescoffre() {
        return descoffre;
    }

    public void setDescoffre(String descoffre) {
        this.descoffre = descoffre;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public double getSalaire() {
        return salaire;
    }

    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }





    public Type_Offre getTypeOffre() {
        return typeOffre;
    }

    public void setTypeOffre(Type_Offre typeOffre) {
        this.typeOffre = typeOffre;
    }

    @Override
    public String toString() {
        return titre + " - " + lieu + " (" + salaire + " TND) - " + typeOffre.getCategorie();
    }
}
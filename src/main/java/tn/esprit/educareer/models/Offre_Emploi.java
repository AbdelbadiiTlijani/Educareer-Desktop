package tn.esprit.educareer.models;

public class Offre_Emploi {
    private int id;
    private String titre;
    private String descoffre;
    private String lieu;
    private double salaire;
    private Type_Offre typeOffre;  // Référence à l'objet Type_Offre
    private int nombre_postulations = 0;  // Valeur par défaut pour nombre_postulations

    // Constructeur avec tous les paramètres
    public Offre_Emploi(String titre, String descoffre, String lieu, double salaire, Type_Offre typeOffre, int nombre_postulations) {
        this.titre = titre;
        this.descoffre = descoffre;
        this.lieu = lieu;
        this.salaire = salaire;
        this.typeOffre = typeOffre;
        this.nombre_postulations = nombre_postulations;
    }

    // Constructeur sans Type_Offre, avec nombre_postulations par défaut
    public Offre_Emploi(String titre, String descoffre, String lieu, double salaire, Type_Offre typeOffre) {
        this.titre = titre;
        this.descoffre = descoffre;
        this.lieu = lieu;
        this.salaire = salaire;
        this.typeOffre = typeOffre;
        this.nombre_postulations = 0;  // Valeur par défaut
    }

    // Constructeur avec id, sans Type_Offre (pour la récupération des données de la BD par exemple)
    public Offre_Emploi(int id, String titre, String descoffre, String lieu, double salaire, Type_Offre typeOffre) {
        this.id = id;
        this.titre = titre;
        this.descoffre = descoffre;
        this.lieu = lieu;
        this.salaire = salaire;
        this.typeOffre = typeOffre;
        this.nombre_postulations = 0;  // Valeur par défaut
    }
    public Offre_Emploi(int id, String titre, String descoffre, String lieu, double salaire, int nombre_postulations) {
        this.id = id;
        this.titre = titre;
        this.descoffre = descoffre;
        this.lieu = lieu;
        this.salaire = salaire;
        this.nombre_postulations = nombre_postulations;
        this.typeOffre = null;
    }
    public Offre_Emploi(int id, String titre, String descoffre, String lieu, double salaire) {
        this.id = id;
        this.titre = titre;
        this.descoffre = descoffre;
        this.lieu = lieu;
        this.salaire = salaire;
        this.nombre_postulations = 0;  // Valeur par défaut
        this.typeOffre = null;  // Pas de Type_Offre pour ce constructeur
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

    public int getNombre_postulations() {
        return nombre_postulations;
    }

    public void setNombre_postulations(int nombre_postulations) {
        this.nombre_postulations = nombre_postulations;
    }

    @Override
    public String toString() {
        return titre + " - " + lieu + " (" + salaire + " TND)";
    }
}

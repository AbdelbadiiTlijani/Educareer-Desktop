package tn.esprit.educareer.models;

public class Projet {


    private int id;
    private int categorie_id ;
    private String description;
    private String titre;
    private String contenu;
    private int formateur_id ;
    private String categorieNom;

    // Getter et Setter
    public String getCategorieNom() {
        return categorieNom;
    }

    public void setCategorieNom(String categorieNom) {
        this.categorieNom = categorieNom;
    }


    public Projet(int categorie_id, String titre,String description, String contenu, int formateur_id) {
        this.categorie_id = categorie_id;
        this.titre = titre;
        this.description= description;
        this.contenu = contenu;
        this.formateur_id = formateur_id;
    }

    public Projet(int id, int categorie_id, String titre,String description, String contenu, int formateur_id) {
        this.id = id;
        this.categorie_id = categorie_id;
        this.titre = titre;
        this.description=description;
        this.contenu = contenu;
        this.formateur_id = formateur_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategorie_id() {
        return categorie_id;
    }

    public void setCategorie_id(int categorie_id) {
        this.categorie_id = categorie_id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    @Override
    public String toString() {
        return "Projet{" +
                "id=" + id +
                ", categorie_id=" + categorie_id +
                ", description='" + description + '\'' +
                ", titre='" + titre + '\'' +
                ", contenu='" + contenu + '\'' +
                ", formateur_id=" + formateur_id +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public int getFormateur_id() {
        return formateur_id;
    }

    public void setFormateur_id(int formateur_id) {
        this.formateur_id = formateur_id;
    }
}

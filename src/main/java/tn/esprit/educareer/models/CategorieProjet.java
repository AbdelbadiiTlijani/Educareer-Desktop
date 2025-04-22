package tn.esprit.educareer.models;

public class CategorieProjet {

    private int id;
    private String categorie;

    public CategorieProjet() {
    }

    private int is_validated;
    private int is_deleted;

    public CategorieProjet(int id, String categorie) {
        this.id = id;
        this.categorie = categorie;
    }

    public CategorieProjet(String nouvelleCat) {
        this.categorie=nouvelleCat;
    }


    @Override
    public String toString() {
        return "CategorieProjet{" +
                "id=" + id +
                ", categorie='" + categorie + '\'' +
                ", is_validated=" + is_validated +
                ", is_deleted=" + is_deleted +
                '}';
    }

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

    public int getIs_validated() {
        return is_validated;
    }

    public void setIs_validated(int is_validated) {
        this.is_validated = is_validated;
    }

    public int getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(int is_deleted) {
        this.is_deleted = is_deleted;
    }

    public CategorieProjet(String categorie, int is_validated, int is_deleted) {
        this.categorie = categorie;
        this.is_validated = is_validated;
        this.is_deleted = is_deleted;
    }

    public CategorieProjet(int id, String categorie, int is_validated, int is_deleted) {
        this.id = id;
        this.categorie = categorie;
        this.is_validated = is_validated;
        this.is_deleted = is_deleted;
    }
}

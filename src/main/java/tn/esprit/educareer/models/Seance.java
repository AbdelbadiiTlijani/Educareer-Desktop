package tn.esprit.educareer.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Seance {
    private int id;
    private String titre;
    private String description;
    private Date date_heure;
    private int duree;
    private Cours cours;

    public Seance(int id, String titre, String description, Date date_heure, int duree, Cours cours) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date_heure = date_heure;
        this.duree = duree;
        this.cours = cours;
    }

    public Seance(String titre, String description, Date date_heure, int duree, Cours cours) {
        this.titre = titre;
        this.description = description;
        this.date_heure = date_heure;
        this.duree = duree;
        this.cours = cours;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date  getDate_heure() {
        return date_heure;
    }

    public void setDate_heure(Date  date_heure) {
        this.date_heure = date_heure;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    @Override
    public String toString() {
        return "Seance{" + "id=" + id + ", titre='" + titre + '\'' + ", description='" + description + '\'' + ", date_heure='" + date_heure + '\'' + ", duree=" + duree + ", cours=" + cours.getNom() + '}';
    }
}

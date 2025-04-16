package tn.esprit.educareer.models;
import java.time.LocalDate;

public class Event {

    private int id;
    private TypeEvent typeEvent; // Relation ManyToOne
    private String titre;
    private String description;
    private LocalDate date;
    private String lieu;
    private int nbrPlace;

    public Event() {
    }

    public Event(int id, TypeEvent typeEvent, String titre, String description, LocalDate date, String lieu, int nbrPlace) {
        this.id = id;
        this.typeEvent = typeEvent;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.lieu = lieu;
        this.nbrPlace = nbrPlace;
    }

    public Event(TypeEvent typeEvent, String titre, String description, LocalDate date, String lieu, int nbrPlace) {
        this.typeEvent = typeEvent;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.lieu = lieu;
        this.nbrPlace = nbrPlace;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeEvent getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(TypeEvent typeEvent) {
        this.typeEvent = typeEvent;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public int getNbrPlace() {
        return nbrPlace;
    }

    public void setNbrPlace(int nbrPlace) {
        this.nbrPlace = nbrPlace;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", typeEvent=" + (typeEvent != null ? typeEvent.getNomE() : "null") +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", lieu='" + lieu + '\'' +
                ", nbrPlace=" + nbrPlace +
                '}';
    }
}

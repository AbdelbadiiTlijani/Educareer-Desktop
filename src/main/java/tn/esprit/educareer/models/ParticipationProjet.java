package tn.esprit.educareer.models;

import java.time.LocalDate;

public class ParticipationProjet {
    private int id;
    private int student_id;
    private int projet_id;
    private LocalDate date_participation;  // Changement ici ✅

    public ParticipationProjet() {
    }

    public ParticipationProjet(int id, int student_id, int projet_id, LocalDate date_participation) {
        this.id = id;
        this.student_id = student_id;
        this.projet_id = projet_id;
        this.date_participation = date_participation;
    }

    public ParticipationProjet(int student_id, int projet_id, LocalDate date_participation) {
        this.student_id = student_id;
        this.projet_id = projet_id;
        this.date_participation = date_participation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public int getProjet_id() {
        return projet_id;
    }

    public void setProjet_id(int projet_id) {
        this.projet_id = projet_id;
    }

    public LocalDate getDate_participation() {
        return date_participation;
    }

    public void setDate_participation(LocalDate date_participation) {
        this.date_participation = date_participation;
    }

    @Override
    public String toString() {
        return "ParticipationProjet{" +
                "id=" + id +
                ", student_id=" + student_id +
                ", projet_id=" + projet_id +
                ", date_participation=" + date_participation +
                '}';
    }
}

package tn.esprit.educareer.models;

public class ReactionCours {
    private int id;
    private String reaction;
    private Cours cours;
    private User student;

    public ReactionCours() {
    }

    public ReactionCours(int id, Cours cours, User user, String reaction) {
        this.id = id;
        this.cours = cours;
        this.student = user;
        this.reaction = reaction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

}

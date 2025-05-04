package tn.esprit.educareer.models;

public class AvisCours {
    private int id;
    private String avis;
    private String classAvis;
    private Cours cours;
    private User student;

    public AvisCours() {
    }

    public AvisCours(int id, Cours cours, User user,String avis, String classAvis) {
        this.id = id;
        this.cours = cours;
        this.student = user;
        this.avis = avis;
        this.classAvis = classAvis;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvis() {
        return avis;
    }

    public void setAvis(String avis) {
        this.avis = avis;
    }

    public String getClassAvis() {
        return classAvis;
    }

    public void setClassAvis(String classAvis) {
        this.classAvis = classAvis;
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

    @Override
    public String toString() {
        return "AvisCours{" + "id=" + id + ", avis='" + avis + '\'' + ", classAvis='" + classAvis + '\'' + ", cours=" + cours + ", student=" + student + '}';
    }
}

package tn.esprit.educareer.models;
import java.time.LocalDate;
public class User {
 private int id , status;
 private String nom , prenom , email , mdp , photo_profil , role , verification_token , date_inscription ;
 private String date ;

    public User( int status, String nom, String prenom, String email, String mdp, String photo_profil, String role, String verification_token, String date_inscription, String date) {
        this.status = status;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.photo_profil = photo_profil;
        this.role = role;
        this.verification_token = verification_token;
        this.date_inscription = date_inscription;
        this.date = date;
    }

    public User(int id, int status, String nom, String prenom, String email, String mdp, String photo_profil, String role, String verification_token, String date_inscription, String date) {
        this.id = id;
        this.status = status;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.photo_profil = photo_profil;
        this.role = role;
        this.verification_token = verification_token;
        this.date_inscription = date_inscription;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getPhoto_profil() {
        return photo_profil;
    }

    public void setPhoto_profil(String photo_profil) {
        this.photo_profil = photo_profil;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getVerification_token() {
        return verification_token;
    }

    public void setVerification_token(String verification_token) {
        this.verification_token = verification_token;
    }

    public String getdate_inscription() {
        return date_inscription;
    }

    public void setdate_inscription(String date_inscription) {
        this.date_inscription = date_inscription;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", status=" + status +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", mdp='" + mdp + '\'' +
                ", photo_profil='" + photo_profil + '\'' +
                ", role='" + role + '\'' +
                ", verification_token='" + verification_token + '\'' +
                ", date_inscription='" + date_inscription + '\'' +
                ", date=" + date +
                '}';
    }
}

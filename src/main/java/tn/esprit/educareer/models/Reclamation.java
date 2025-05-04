package tn.esprit.educareer.models;

import java.time.LocalDateTime;

public class Reclamation {
    private int id;
    private TypeReclamation typeReclamation;
    private String sujet;
    private String description;
    private LocalDateTime createdAt;

    // Constructeurs
    public Reclamation() {}

    public Reclamation(int id, TypeReclamation typeReclamation, String sujet, String description, LocalDateTime createdAt) {
        this.id = id;
        this.typeReclamation = typeReclamation;
        this.sujet = sujet;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeReclamation getTypeReclamation() {
        return typeReclamation;
    }

    public void setTypeReclamation(TypeReclamation typeReclamation) {
        this.typeReclamation = typeReclamation;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", typeReclamation=" + typeReclamation +
                ", sujet='" + sujet + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

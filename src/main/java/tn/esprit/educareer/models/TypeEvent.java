package tn.esprit.educareer.models;

public class TypeEvent {


    private int id;
    private String nomE;

    public TypeEvent() {
    }

    public TypeEvent(int id, String nomE) {
        this.id = id;
        this.nomE = nomE;
    }

    public TypeEvent(String nomE) {
        this.nomE = nomE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomE() {
        return nomE;
    }

    public void setNomE(String nomE) {
        this.nomE = nomE;
    }

    @Override
    public String toString() {
        return nomE; // important pour afficher dans ComboBox automatiquement
    }
}

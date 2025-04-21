package tn.esprit.educareer.models;

import java.util.Date;

public class Messages {
     private int id;
     private int sender_id;
     private int recipient_id;
     private String titre;
     private String message;
     private Date created_at  ;
     private boolean is_read ;

    public Messages(int id, int sender_id, int recipient_id, String titre, String message, Date created_at, boolean is_read) {
        this.id = id;
        this.sender_id = sender_id;
        this.recipient_id = recipient_id;
        this.titre = titre;
        this.message = message;
        this.created_at = created_at;
        this.is_read = is_read;
    }

    public Messages(int sender_id, int recipient_id, String titre, String message, Date created_at, boolean is_read) {
        this.sender_id = sender_id;
        this.recipient_id = recipient_id;
        this.titre = titre;
        this.message = message;
        this.created_at = created_at;
        this.is_read = is_read;
    }

    public Messages() {
    }

    @Override
    public String toString() {
        return "Messages{" +
                "id=" + id +
                ", sender_id=" + sender_id +
                ", recipient_id=" + recipient_id +
                ", titre='" + titre + '\'' +
                ", message='" + message + '\'' +
                ", created_at=" + created_at +
                ", is_read=" + is_read +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getRecipient_id() {
        return recipient_id;
    }

    public void setRecipient_id(int recipient_id) {
        this.recipient_id = recipient_id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }
}

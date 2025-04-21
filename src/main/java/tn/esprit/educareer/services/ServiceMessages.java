package tn.esprit.educareer.services;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.models.Messages;
import tn.esprit.educareer.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceMessages {

    private Connection cnx;
    private static ServiceMessages instance;

    private ServiceMessages() {
        cnx = MyConnection.getInstance().getCnx();
    }

    public static ServiceMessages getInstance() {
        if (instance == null)
            instance = new ServiceMessages();
        return instance;
    }

    public void addMessage(Messages msg) {
        String sql = "INSERT INTO messages (sender_id, recipient_id, titre, message, created_at, is_read) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, msg.getSender_id());
            ps.setInt(2, msg.getRecipient_id());
            ps.setString(3, msg.getTitre());
            ps.setString(4, msg.getMessage());
            ps.setTimestamp(5, new java.sql.Timestamp(msg.getCreated_at().getTime()));
            ps.setBoolean(6, msg.isIs_read());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Messages> getMessages(int senderId, int recipientId) {
        List<Messages> messages = new ArrayList<>();
        try  {
            String query = "SELECT * FROM messages WHERE (sender_id = ? AND recipient_id = ?) OR (sender_id = ? AND recipient_id = ?) ORDER BY created_at";
            try (PreparedStatement statement = cnx.prepareStatement(query)) {
                statement.setInt(1, senderId);
                statement.setInt(2, recipientId);
                statement.setInt(3, recipientId);
                statement.setInt(4, senderId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int sender = resultSet.getInt("sender_id");
                    int recipient = resultSet.getInt("recipient_id");
                    String titre = resultSet.getString("titre");
                    String messageContent = resultSet.getString("message");
                    Date dateSent = resultSet.getDate("created_at");
                    boolean isRead = resultSet.getBoolean("is_read");
                    messages.add(new Messages(sender, recipient, titre, messageContent, dateSent, isRead));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    public User getUserByEmail(String email) {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getInt("status"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mdp"),
                        rs.getString("photo_profil"),
                        rs.getString("role"),
                        rs.getString("verification_token"),
                        rs.getString("date_inscription"),
                        rs.getString("date")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserById(int id) {
        String query = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getInt("status"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mdp"),
                        rs.getString("photo_profil"),
                        rs.getString("role"),
                        rs.getString("verification_token"),
                        rs.getString("date_inscription"),
                        rs.getString("date")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

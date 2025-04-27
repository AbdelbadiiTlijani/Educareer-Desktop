package tn.esprit.educareer.services;

import tn.esprit.educareer.models.AvisCours;
import tn.esprit.educareer.models.Cours;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.utils.MyConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServiceAvisCours {
    Connection cnx;
    public ServiceAvisCours() {
        cnx = MyConnection.getInstance().getCnx();
    }


    public void ajouterAvis(Cours cours, String avisText, String resultatAnalyse, User student) {
        try {
            String sql = "INSERT INTO avis_cours (cours_id, student_id, avis, class_avis) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = cnx.prepareStatement(sql);
            statement.setInt(1, cours.getId());
            statement.setInt(2, student.getId());
            statement.setString(3, avisText);
            statement.setString(4, resultatAnalyse);

            statement.executeUpdate();
            System.out.println("Avis ajouté avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public String getAvis(String text) {
        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String urlString = "http://127.0.0.1:5000/predict?review=" + encodedText;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                conn.disconnect();
                return content.toString();
            } else {
                conn.disconnect();
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}

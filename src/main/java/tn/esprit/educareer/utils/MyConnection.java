package tn.esprit.educareer.utils;

import java.sql.*;

public class MyConnection {
    private static MyConnection instance;
    private final String URL ="jdbc:mysql://127.0.0.1:3306/pi-integration";
    private final String USERNAME ="root";
    private final String PASSWORD = "";
    private Connection  cnx ;

    private MyConnection() {
        try {
            cnx = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connection established");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static MyConnection getInstance() {
        if (instance == null)
            instance = new MyConnection();
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }
}
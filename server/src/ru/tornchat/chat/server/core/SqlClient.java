package ru.tornchat.chat.server.core;

import java.sql.*;

public class SqlClient {

    private static Connection connection;
    private static Statement statement;

    synchronized static void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:ChatDB.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    synchronized static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    synchronized static String getNickname(String login, String password){
        String request = "SELECT name FROM users WHERE login ='" + login +
                "' AND password ='" + password + "'";

        try (ResultSet set = statement.executeQuery(request)){
            if(set.next()){
                return set.getString(1);
            } else {
                return null;
            }
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }

}

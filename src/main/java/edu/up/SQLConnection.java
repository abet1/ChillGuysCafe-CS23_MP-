package edu.up;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnection {
    private static final String DATABASE_URL = "jdbc:sqlite:chillguys.db";

    public static Connection getConnection() {
        try{
            Connection connection = DriverManager.getConnection(DATABASE_URL);
            return connection;
        }catch(SQLException e){
            System.out.println("Error connecting to database"+e.getMessage());
            return null;
        }
    }
}

package com.dbconnectivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {

    public static Connection getdbConnection() throws Exception {
        System.out.println("In getdbConnection...");
        Connection con = null;

        Properties pr = new Properties();
        pr.load(DBConnection.class.getClassLoader().getResourceAsStream("dbconfig.properties"));

        if (pr != null) {
            Class.forName(pr.getProperty("driver"));
            System.out.println("Driver Loaded......");
            con = DriverManager.getConnection(pr.getProperty("dburl"), pr.getProperty("user"), pr.getProperty("password"));
            System.out.println("Connection Established......");
        } 
        else {
            System.out.println(".properties file is not found...");
        }
        return con;
    }
}

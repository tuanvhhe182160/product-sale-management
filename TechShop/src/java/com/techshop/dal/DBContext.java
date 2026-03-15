/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techshop.dal;

import java.util.ResourceBundle;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author justi
 */
public class DBContext {
    protected Connection connection;

    public DBContext() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("com.techshop.conf.db");
            
            String serverName = bundle.getString("serverName");
            String portNumber = bundle.getString("portNumber");
            String dbName = bundle.getString("dbName");
            String userID = bundle.getString("userID");
            String password = bundle.getString("password");

            String url = "jdbc:sqlserver://" + serverName + ":" + portNumber + 
                         ";databaseName=" + dbName + 
                         ";encrypt=true;trustServerCertificate=true;";

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, userID, password);
            
        } catch (ClassNotFoundException e) {
            System.out.println("Lỗi: Không tìm thấy Driver! Check lại file mssql-jdbc.jar");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Lỗi: Không kết nối được SQL! Check lại user/pass trong db.properties");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        DBContext db = new DBContext();
        if(db.connection != null) {
            System.out.println("KẾT NỐI DATABASE THÀNH CÔNG!");
        } else {
            System.err.println("KẾT NỐI THẤT BẠI.");
        }
    }
}

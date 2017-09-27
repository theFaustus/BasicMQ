/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ipascari
 */
public class DBConnector {

    private Connection conn = null;

    public DBConnector(String dbUrl, String user, String pw, boolean sslIsOn) {
        Properties p = new Properties();
        p.setProperty("user", user);
        p.setProperty("password", pw);
        if (sslIsOn) {
            p.setProperty("ssl", "true");
        }
        try {
            conn = DriverManager.getConnection(dbUrl, p);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public void close() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

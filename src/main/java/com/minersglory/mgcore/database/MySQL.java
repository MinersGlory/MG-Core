package com.minersglory.mgcore.database;

import com.minersglory.mgcore.MGCore;

import java.sql.*;
import java.util.logging.Level;

public class MySQL extends ConnectionHandler {

    private final String user;
    private final String database;
    private final String password;
    private final String host;
    private final Integer port;

    private Connection connection;

    public MySQL(MGCore plugin, String host, Integer port, String database, String user, String password) {
        super(plugin);
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.connection = null;
    }

    @Override
    public Connection openConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true&user=" + this.user + "&password=" + this.password);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Connection to MySQL failed due to: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "JDBC driver does not exist!");
        }
        return connection;
    }

    @Override
    public boolean checkConnection() {
        return connection != null;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "MySQL connection failed to close.");
                ex.printStackTrace();
            }
        }
    }

    public ResultSet sqlQuery(String query) {
        Connection conn = null;

        if (checkConnection()) {
            conn = getConnection();
        } else {
            conn = openConnection();
        }

        Statement s = null;

        try {
            s = conn.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        ResultSet rs = null;

        try {
            rs = s.executeQuery(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        closeConnection();

        return rs;
    }

    public void sqlUpdate(String update) {
        Connection conn = null;

        if (checkConnection()) {
            conn = getConnection();
        } else {
            conn = openConnection();
        }

        Statement s = null;

        try {
            s = conn.createStatement();
            s.executeUpdate(update);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        closeConnection();
    }
}
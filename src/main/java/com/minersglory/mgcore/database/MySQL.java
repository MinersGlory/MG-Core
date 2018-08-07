package com.minersglory.mgcore.database;

import com.minersglory.mgcore.MGCore;

import java.sql.*;

public class MySQL {

    private final String mySQLDatabase;
    private final String mySQLUsername;
    private final String mySQLPassword;
    private ConnectionPool pool;
    private MGCore plugin;

    public MySQL(String database, String username, String password, MGCore plugin) throws ClassNotFoundException, SQLException {
        this.mySQLDatabase = database;
        this.mySQLUsername = username;
        this.mySQLPassword = password;
        this.plugin = plugin;
        Class.forName("com.mysql.jdbc.Driver");
        pool = new ConnectionPool(this.mySQLDatabase + "?autoReconnect=true&user=" + this.mySQLUsername + "&password=" + this.mySQLPassword);

        this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {


            public void run() {
                pool.resetLoads();
            }

        }, 100, 100);
    }

    public PreparedStatement getFreshlyPreparedStatement(String query) throws SQLException {
        return pool.getConnection().prepareStatement(query);
    }

    public PreparedStatement getFreshlyPreparedStatementWithKeys(String query) throws SQLException {
        return pool.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    }

    public Connection getConnection() throws SQLException {
        return pool.getConnection();
    }

    public ConnectionPool getPool() {
        return pool;
    }
}

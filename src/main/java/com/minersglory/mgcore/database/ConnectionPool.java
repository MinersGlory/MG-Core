package com.minersglory.mgcore.database;

import com.minersglory.mgcore.managers.CoreManager;

import java.io.Closeable;
import java.sql.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool implements Closeable {

    private final static int poolSize = 4;
    private final JDCConnection[] connections;
    private final String url;
    private final Lock lock = new ReentrantLock();

    public ConnectionPool(String url) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        this.url = url;
        connections = new JDCConnection[poolSize];
        for (int i = 0; i < connections.length; i++) {
            connections[i] = new JDCConnection(DriverManager.getConnection(url));
        }
    }


    public void close() {
        lock.lock();
        for (JDCConnection connection : connections) {
            connection.terminate();
        }
        lock.unlock();
    }

    public void resetLoads() {
        lock.lock();
        for (JDCConnection connection : connections) {
            connection.setLoad(0);
        }
        lock.unlock();
    }

    public JDCConnection[] getConnections() {
        return this.connections;
    }

    public Connection getConnection() throws SQLException {
        lock.lock();
        try {
            int lowestIndex = 0;
            int lowestLoad = Integer.MAX_VALUE;
            for (int i = 0; i < connections.length; i++) {
                JDCConnection connection = connections[i];
                if (!connection.getConnection().isValid(1) {
                    connection.terminate();
                    connections[i] = new JDCConnection(DriverManager.getConnection(url));
                }
                if (connection.getLoad() < lowestLoad) {
                    lowestLoad = connection.getLoad();
                    lowestIndex = i;
                }
            }

            connections[lowestIndex].incrementLoad();
            return connections[lowestIndex].getConnection();
        } finally {
            lock.unlock();
        }
    }

    public class JDCConnection {
        private final Connection conn;
        private int load = 0;

        JDCConnection(Connection conn) {
            this.conn = conn;
        }

        public void close() {
            try {
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true);
                }
            } catch (final SQLException ex) {
                terminate();
            }
        }

        synchronized Connection getConnection() {
            return this.conn;
        }

        public int getLoad() {
            return this.load;
        }

        public void setLoad(int load) {
            this.load = load;
        }

        public void incrementLoad() {
            incrementLoad(1);
        }

        public void incrementLoad(int factor) {
            this.load += factor;
        }

        void terminate() {
            try {
                conn.close();
            } catch (final SQLException ex) {
                CoreManager.getCore().getLogger().warning("SQLException while terminating pool connection: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}

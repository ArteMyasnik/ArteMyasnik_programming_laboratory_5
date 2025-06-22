package com.artemyasnik.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ConnectionFactory {
    private static final Logger log = LoggerFactory.getLogger(ConnectionFactory.class);
    private static volatile ConnectionFactory instance;
    private static final Lock instanceLock = new ReentrantLock();

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;
    private Connection connection;

    private ConnectionFactory() {
        Properties config = loadConfig();
        this.dbUrl = config.getProperty("db.url");
        this.dbUser = config.getProperty("db.user");
        this.dbPassword = config.getProperty("db.password");
        initializeConnection();
    }

    private static Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream input = ConnectionFactory.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new IllegalStateException("db.properties not found in classpath!");
            }
            props.load(input);
            String user = props.getProperty("db.user");
            if (user != null && user.contains("${")) {
                props.setProperty("db.user", System.getProperty("user.name"));
            }

            log.info("Loaded DB configuration for URL: {}", props.getProperty("db.url"));
            return props;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    private void initializeConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            log.info("Database connection established for user: {}", dbUser);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("PostgreSQL JDBC driver not found", e);
        } catch (SQLException e) {
            throw new RuntimeException(String.format(
                    "Failed to establish database connection to %s as user %s",
                    dbUrl, dbUser), e);
        }
    }

    public static ConnectionFactory getInstance() throws SQLException {
        ConnectionFactory localInstance = instance;
        if (localInstance == null || localInstance.connection.isClosed()) {
            instanceLock.lock();
            try {
                localInstance = instance;
                if (localInstance == null || localInstance.connection.isClosed()) {
                    instance = new ConnectionFactory();
                    localInstance = instance;
                }
            } finally {
                instanceLock.unlock();
            }
        }
        return localInstance;
    }

    public Connection getConnection() throws SQLException {
        if (connection.isClosed()) {
            initializeConnection();
        }
        return connection;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                log.info("Database connection closed");
            } catch (SQLException e) {
                log.warn("Failed to close database connection", e);
            }
        }
    }
}
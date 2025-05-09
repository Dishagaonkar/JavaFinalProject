package monopoly.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:monopoly.db";

    public static void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC"); // ✅ load the SQLite driver manually
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
    
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            // Create users table
            String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL UNIQUE, " +
                    "password TEXT NOT NULL);";
            stmt.execute(createUsersTableSQL);

            // Create games table
            String createGamesTableSQL = "CREATE TABLE IF NOT EXISTS games (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "winner TEXT NOT NULL);";
            stmt.execute(createGamesTableSQL);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Record winner

    public static boolean createUser(String username, String password) {

        try (Connection conn = DriverManager.getConnection(URL);
         PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {

        byte[] salt = PasswordUtils.generateSalt();
        String hashed = PasswordUtils.hashPassword(password, salt);

        stmt.setString(1, username);
        stmt.setString(2, hashed);
        stmt.executeUpdate();
        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
    // try (Connection conn = DriverManager.getConnection(URL);
    //      PreparedStatement stmt = conn.prepareStatement("INSERT INTO users(username, password) VALUES (?, ?)")) {

    //     stmt.setString(1, username);
    //     stmt.setString(2, password);
    //     stmt.executeUpdate();
    //     return true;
    // } catch (SQLException e) {
    //     return false; // username already exists or error
    // }
}

    public static boolean authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(URL);
         PreparedStatement stmt = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {

        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String storedHash = rs.getString("password");
            return PasswordUtils.verifyPassword(password, storedHash);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
    // try (Connection conn = DriverManager.getConnection(URL);
    //      PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {

    //     stmt.setString(1, username);
    //     stmt.setString(2, password);
    //     ResultSet rs = stmt.executeQuery();
    //     return rs.next();
    // } catch (SQLException e) {
    //     e.printStackTrace();
    //     return false;
    // }
}

    public static void recordWinner(String playerName) {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            String insertSQL = "INSERT INTO games (winner) VALUES ('" + playerName + "');";
            stmt.executeUpdate(insertSQL);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

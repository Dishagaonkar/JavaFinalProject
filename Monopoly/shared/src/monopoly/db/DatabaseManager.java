package monopoly.db;

import java.sql.Connection;
import java.sql.DriverManager;
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
    
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS games (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    winner TEXT NOT NULL,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
                );
            """;
            stmt.execute(createTableSQL);
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Record winner
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

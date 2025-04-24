package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/bdd_shopping";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // ✅ Fournit une NOUVELLE connexion à chaque appel
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion ouverte.");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion : " + e.getMessage());
            return null;
        }
    }

    // ❌ On ne gère plus de fermeture globale ici
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("🔌 Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Erreur lors de la fermeture : " + e.getMessage());
        }
    }
}

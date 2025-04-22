package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Informations de connexion à adapter si besoin
    private static final String URL = "jdbc:mysql://localhost:3306/bdd_shopping";
    private static final String USER = "root"; // à adapter selon ton MySQL
    private static final String PASSWORD = ""; // idem

    private static Connection connection;

    // Méthode pour récupérer la connexion (singleton)
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion réussie à la base de données.");
            } catch (SQLException e) {
                System.err.println("❌ Erreur de connexion : " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("🔌 Connexion fermée.");
            } catch (SQLException e) {
                System.err.println("⚠️ Erreur lors de la fermeture : " + e.getMessage());
            }
        }
    }
}

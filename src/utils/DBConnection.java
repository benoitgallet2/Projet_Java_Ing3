package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fournit une connexion à la base de données MySQL.
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/bdd_shopping";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    /**
     * Ouvre et retourne une nouvelle connexion à la base de données.
     *
     * @return une connexion JDBC active ou null en cas d'erreur
     */
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion ouverte.");
            return conn;
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
            return null;
        }
    }

    /**
     * Ferme proprement une connexion si elle est encore ouverte.
     *
     * @param conn la connexion à fermer
     */
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture : " + e.getMessage());
        }
    }
}

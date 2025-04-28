package dao;

import model.Compte;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompteDAO {

    // Créer un compte (retourne l'ID généré ou -1 si erreur)
    public int create(Compte compte) {
        String sql = "INSERT INTO Compte (login, mdp, admin) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, compte.getLogin());
            stmt.setString(2, compte.getMdp());
            stmt.setBoolean(3, compte.isAdmin());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // retourne l'id_user généré
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la création du compte : " + e.getMessage());
        }

        return -1;
    }

    // Récupérer un compte par login
    public Compte findByLogin(String login) {
        String sql = "SELECT * FROM Compte WHERE login = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Compte(
                        rs.getInt("id_user"),
                        rs.getString("login"),
                        rs.getString("mdp"),
                        rs.getBoolean("admin")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du compte : " + e.getMessage());
        }

        return null;
    }

    public List<Compte> findAll() {
        List<Compte> comptes = new ArrayList<>();
        String sql = "SELECT * FROM Compte";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Compte compte = new Compte(
                        rs.getInt("id_user"),
                        rs.getString("login"),
                        rs.getString("mdp"),
                        rs.getBoolean("admin")
                );
                comptes.add(compte);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur findAll comptes : " + e.getMessage());
        }

        return comptes;
    }

    // ❌ Supprimer un compte par son ID
    public boolean delete(int idUser) {
        Connection conn = DBConnection.getConnection();

        try {
            // 1. Supprimer dans Panier
            String deletePanier = "DELETE FROM Panier WHERE id_user = ?";
            try (PreparedStatement stmtPanier = conn.prepareStatement(deletePanier)) {
                stmtPanier.setInt(1, idUser);
                stmtPanier.executeUpdate();
            }

            // 2. Supprimer les lignes dans Articles_Commandes liées aux commandes du user
            String deleteArticlesCommandes = """
            DELETE ac FROM Articles_Commandes ac
            JOIN Commande c ON ac.id_commande = c.id_commande
            WHERE c.id_user = ?
        """;
            try (PreparedStatement stmtAC = conn.prepareStatement(deleteArticlesCommandes)) {
                stmtAC.setInt(1, idUser);
                stmtAC.executeUpdate();
            }

            // 3. Supprimer les Commandes
            String deleteCommandes = "DELETE FROM Commande WHERE id_user = ?";
            try (PreparedStatement stmtCommandes = conn.prepareStatement(deleteCommandes)) {
                stmtCommandes.setInt(1, idUser);
                stmtCommandes.executeUpdate();
            }

            // 4. Supprimer Client
            String deleteClient = "DELETE FROM Client WHERE id_user = ?";
            try (PreparedStatement stmtClient = conn.prepareStatement(deleteClient)) {
                stmtClient.setInt(1, idUser);
                stmtClient.executeUpdate();
            }

            // 5. Supprimer Compte
            String deleteCompte = "DELETE FROM Compte WHERE id_user = ?";
            try (PreparedStatement stmtCompte = conn.prepareStatement(deleteCompte)) {
                stmtCompte.setInt(1, idUser);
                return stmtCompte.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression compte : " + e.getMessage());
            return false;
        }
    }


    public boolean updateAdminStatus(int idUser, boolean isAdmin) {
        String sql = "UPDATE Compte SET admin = ? WHERE id_user = ?";
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();

            if (conn == null || conn.isClosed()) {
                System.err.println("❌ Connexion nulle ou déjà fermée.");
                return false;
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, isAdmin);
            stmt.setInt(2, idUser);

            int updated = stmt.executeUpdate();
            stmt.close(); // on ferme nous-même

            System.out.println("✅ Admin modifié : ID " + idUser + " => " + isAdmin);
            return updated > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL dans updateAdminStatus : " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException e) {
                System.err.println("⚠️ Erreur fermeture de connexion : " + e.getMessage());
            }
        }
    }

    public Compte findById(int idUser) {
        String sql = "SELECT * FROM Compte WHERE id_user = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Compte(
                        rs.getInt("id_user"),
                        rs.getString("login"),
                        rs.getString("mdp"),
                        rs.getBoolean("admin")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération compte par ID : " + e.getMessage());
        }

        return null;
    }



}

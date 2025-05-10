package dao;

import model.Compte;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour les comptes utilisateurs.
 * Gère les opérations CRUD sur la table Compte ainsi que la suppression en cascade d’un utilisateur.
 */
public class CompteDAO {

    /**
     * Crée un nouveau compte dans la base.
     *
     * @param compte Compte à créer.
     * @return L’ID généré pour ce compte, ou -1 en cas d’échec.
     */
    public int create(Compte compte) {
        String sql = "INSERT INTO Compte (login, mdp, admin) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, compte.getLogin());
            stmt.setString(2, compte.getMdp());
            stmt.setBoolean(3, compte.isAdmin());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du compte : " + e.getMessage());
        }

        return -1;
    }

    /**
     * Recherche un compte par son login.
     *
     * @param login Le login à chercher.
     * @return Le compte correspondant, ou null si non trouvé.
     */
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
            System.err.println("Erreur lors de la récupération du compte : " + e.getMessage());
        }

        return null;
    }

    /**
     * Récupère tous les comptes existants.
     *
     * @return Une liste de tous les comptes.
     */
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
            System.err.println("Erreur findAll comptes : " + e.getMessage());
        }

        return comptes;
    }

    /**
     * Supprime un compte ainsi que toutes les données liées :
     * panier, commandes, articles_commandes, client, etc.
     *
     * @param idUser L’identifiant du compte.
     * @return true si la suppression a réussi.
     */
    public boolean delete(int idUser) {
        Connection conn = DBConnection.getConnection();

        try {
            // Supprimer panier
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Panier WHERE id_user = ?")) {
                stmt.setInt(1, idUser);
                stmt.executeUpdate();
            }

            // Supprimer articles_commandes via les commandes
            String deleteArticlesCommandes = """
                DELETE ac FROM Articles_Commandes ac
                JOIN Commande c ON ac.id_commande = c.id_commande
                WHERE c.id_user = ?
            """;
            try (PreparedStatement stmt = conn.prepareStatement(deleteArticlesCommandes)) {
                stmt.setInt(1, idUser);
                stmt.executeUpdate();
            }

            // Supprimer commandes
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Commande WHERE id_user = ?")) {
                stmt.setInt(1, idUser);
                stmt.executeUpdate();
            }

            // Supprimer client
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Client WHERE id_user = ?")) {
                stmt.setInt(1, idUser);
                stmt.executeUpdate();
            }

            // Supprimer compte
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Compte WHERE id_user = ?")) {
                stmt.setInt(1, idUser);
                return stmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Erreur suppression compte : " + e.getMessage());
            return false;
        }
    }

    /**
     * Met à jour le statut administrateur d’un compte.
     *
     * @param idUser  ID du compte.
     * @param isAdmin Nouveau statut.
     * @return true si la mise à jour a réussi.
     */
    public boolean updateAdminStatus(int idUser, boolean isAdmin) {
        String sql = "UPDATE Compte SET admin = ? WHERE id_user = ?";
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            if (conn == null || conn.isClosed()) {
                System.err.println("Connexion nulle ou déjà fermée.");
                return false;
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, isAdmin);
            stmt.setInt(2, idUser);
            int updated = stmt.executeUpdate();
            stmt.close();

            System.out.println("Admin modifié : ID " + idUser + " => " + isAdmin);
            return updated > 0;

        } catch (SQLException e) {
            System.err.println("Erreur SQL dans updateAdminStatus : " + e.getMessage());
            return false;

        } finally {
            try {
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture de connexion : " + e.getMessage());
            }
        }
    }

    /**
     * Récupère un compte par son ID.
     *
     * @param idUser L’identifiant recherché.
     * @return Le compte correspondant ou null.
     */
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
            System.err.println("Erreur récupération compte par ID : " + e.getMessage());
        }

        return null;
    }
}

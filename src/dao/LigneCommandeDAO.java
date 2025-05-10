package dao;

import model.LigneCommande;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour les lignes de commande (liaison entre commandes et articles).
 */
public class LigneCommandeDAO {

    /**
     * Récupère toutes les lignes d'une commande donnée.
     *
     * @param idCommande L’ID de la commande.
     * @return Liste des lignes associées à cette commande.
     */
    public List<LigneCommande> findByCommandeId(int idCommande) {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT * FROM Articles_Commandes WHERE id_commande = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCommande);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LigneCommande ligne = new LigneCommande(
                        rs.getInt("id_commande"),
                        rs.getInt("id_article")
                );
                lignes.add(ligne);
            }

        } catch (Exception e) {
            System.err.println("Erreur récupération lignes de commande : " + e.getMessage());
        }

        return lignes;
    }

    /**
     * Ajoute une ligne à la table Articles_Commandes.
     *
     * @param idCommande L’ID de la commande.
     * @param idArticle  L’ID de l’article.
     * @return true si l’insertion a réussi.
     */
    public boolean addArticleToCommande(int idCommande, int idArticle) {
        String sql = "INSERT INTO Articles_Commandes (id_commande, id_article) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCommande);
            stmt.setInt(2, idArticle);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Erreur ajout article à commande : " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupère tous les IDs d’articles ayant été commandés.
     *
     * @return Liste des IDs d’articles.
     */
    public List<Integer> getAllArticleIds() {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_article FROM Articles_Commandes";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("id_article"));
            }

        } catch (Exception e) {
            System.err.println("Erreur récupération des articles commandés : " + e.getMessage());
        }

        return ids;
    }

    /**
     * Récupère toutes les lignes de commandes existantes.
     *
     * @return Liste de toutes les lignes Articles_Commandes.
     */
    public List<LigneCommande> findAll() {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT * FROM Articles_Commandes";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                LigneCommande ligne = new LigneCommande(
                        rs.getInt("id_commande"),
                        rs.getInt("id_article")
                );
                lignes.add(ligne);
            }

        } catch (Exception e) {
            System.err.println("Erreur récupération de toutes les lignes de commande : " + e.getMessage());
        }

        return lignes;
    }

    /**
     * Compte le nombre de commandes distinctes dans lesquelles un article est apparu.
     *
     * @param idArticle L’ID de l’article.
     * @return Le nombre de commandes uniques contenant cet article.
     */
    public int countDistinctCommandesByArticle(int idArticle) {
        String sql = "SELECT COUNT(DISTINCT id_commande) FROM Articles_Commandes WHERE id_article = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idArticle);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            System.err.println("Erreur comptage des commandes : " + e.getMessage());
        }

        return 0;
    }
}

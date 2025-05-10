package dao;

import model.ArticlesCommandes;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la table Articles_Commandes.
 * Permet l'ajout et la récupération des liaisons entre commandes et articles.
 * Elle fait la liste des articles d'une commande
 */
public class ArticlesCommandesDAO {

    /**
     * Ajoute une ligne dans la table Articles_Commandes.
     *
     * @param ac L'objet ArticlesCommandes contenant id_commande et id_article.
     * @return true si l'insertion a réussi, false sinon.
     */
    public boolean add(ArticlesCommandes ac) {
        String sql = "INSERT INTO Articles_Commandes (id_commande, id_article) VALUES (?, ?)";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, ac.getIdCommande());
            stmt.setInt(2, ac.getIdArticle());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'article à la commande : " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupère la liste des articles associés à une commande.
     *
     * @param idCommande L'identifiant de la commande.
     * @return Liste d'objets ArticlesCommandes pour cette commande.
     */
    public List<ArticlesCommandes> findArticlesByCommande(int idCommande) {
        List<ArticlesCommandes> list = new ArrayList<>();
        String sql = "SELECT * FROM Articles_Commandes WHERE id_commande = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idCommande);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ArticlesCommandes ac = new ArticlesCommandes(
                        rs.getInt("id_commande"),
                        rs.getInt("id_article")
                );
                list.add(ac);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des articles de la commande : " + e.getMessage());
        }

        return list;
    }
}

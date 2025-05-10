package dao;

import model.Panier;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * DAO pour gérer les opérations liées au panier d'un utilisateur.
 */
public class PanierDAO {

    /**
     * Ajoute un article dans le panier.
     *
     * @param panier Objet représentant l'article et l'utilisateur.
     * @return true si l’ajout a réussi.
     */
    public boolean add(Panier panier) {
        String sql = "INSERT INTO Panier (id_user, id_article) VALUES (?, ?)";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, panier.getIdUser());
            stmt.setInt(2, panier.getIdArticle());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout dans le panier : " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime une seule ligne du panier pour un article donné (même s’il y a plusieurs exemplaires).
     *
     * @param panier Objet contenant id_user et id_article.
     * @return true si la suppression a réussi.
     */
    public boolean remove(Panier panier) {
        String sql = "DELETE FROM Panier WHERE id_user = ? AND id_article = ? LIMIT 1";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, panier.getIdUser());
            stmt.setInt(2, panier.getIdArticle());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du panier : " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime tous les articles d’un utilisateur dans le panier.
     *
     * @param idUser ID de l'utilisateur.
     * @return true si la suppression a réussi.
     */
    public boolean clearUserPanier(int idUser) {
        String sql = "DELETE FROM Panier WHERE id_user = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors du vidage du panier : " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupère tous les articles dans le panier d’un utilisateur.
     *
     * @param idUser ID de l'utilisateur.
     * @return Liste des objets Panier.
     */
    public List<Panier> getPanierByUser(int idUser) {
        List<Panier> panierList = new ArrayList<>();
        String sql = "SELECT * FROM Panier WHERE id_user = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Panier panier = new Panier(
                        rs.getInt("id_user"),
                        rs.getInt("id_article")
                );
                panierList.add(panier);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du panier : " + e.getMessage());
        }

        return panierList;
    }

    /**
     * Récupère les quantités de chaque article dans le panier d’un utilisateur.
     *
     * @param idUser ID de l'utilisateur.
     * @return Map des articles avec leur quantité.
     */
    public Map<Integer, Integer> getQuantitesByUser(int idUser) {
        Map<Integer, Integer> quantites = new HashMap<>();
        String sql = "SELECT id_article, COUNT(*) AS quantite FROM Panier WHERE id_user = ? GROUP BY id_article";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idArticle = rs.getInt("id_article");
                int quantite = rs.getInt("quantite");
                quantites.put(idArticle, quantite);
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération quantités : " + e.getMessage());
        }

        return quantites;
    }
}

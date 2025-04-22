package dao;

import model.Panier;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanierDAO {

    // Ajouter un article dans le panier (ajout simple d'une ligne)
    public boolean add(Panier panier) {
        String sql = "INSERT INTO Panier (id_user, id_article) VALUES (?, ?)";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, panier.getIdUser());
            stmt.setInt(2, panier.getIdArticle());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout dans le panier : " + e.getMessage());
            return false;
        }
    }

    // Supprimer une ligne spécifique du panier (un article parmi d'autres)
    public boolean remove(Panier panier) {
        String sql = "DELETE FROM Panier WHERE id_user = ? AND id_article = ? LIMIT 1";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, panier.getIdUser());
            stmt.setInt(2, panier.getIdArticle());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du panier : " + e.getMessage());
            return false;
        }
    }

    // Vider entièrement le panier d’un utilisateur
    public boolean clearUserPanier(int idUser) {
        String sql = "DELETE FROM Panier WHERE id_user = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du vidage du panier : " + e.getMessage());
            return false;
        }
    }

    // Récupérer tous les articles d’un panier utilisateur
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
            System.err.println("❌ Erreur lors de la récupération du panier : " + e.getMessage());
        }

        return panierList;
    }
}

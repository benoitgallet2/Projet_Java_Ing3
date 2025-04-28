package dao;

import model.LigneCommande;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LigneCommandeDAO {

    // 🔍 Récupérer tous les articles d'une commande donnée
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
            System.err.println("❌ Erreur récupération lignes de commande : " + e.getMessage());
        }

        return lignes;
    }

    // ➕ Ajouter une ligne de commande (article dans une commande)
    public boolean addArticleToCommande(int idCommande, int idArticle) {
        String sql = "INSERT INTO Articles_Commandes (id_commande, id_article) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCommande);
            stmt.setInt(2, idArticle);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("❌ Erreur ajout article à commande : " + e.getMessage());
            return false;
        }
    }

    // 📋 Récupérer tous les IDs d'articles commandés
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
            System.err.println("❌ Erreur récupération des articles commandés : " + e.getMessage());
        }

        return ids;
    }

    // 🔁 Récupérer toutes les lignes de commande (nécessaire pour les alertes)
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
            System.err.println("❌ Erreur récupération de toutes les lignes de commande : " + e.getMessage());
        }

        return lignes;
    }
}

package dao;

import model.Article;
import utils.DBConnection;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAO {

    // INSERT : ajouter un article
    public boolean create(Article article) {
        String sql = "INSERT INTO Article (nom_article, prix_unite, prix_vrac, modulo_reduction, marque, " +
                "quantite_dispo, image, note, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, article.getNomArticle());
            stmt.setDouble(2, article.getPrixUnite());
            stmt.setDouble(3, article.getPrixVrac());
            stmt.setInt(4, article.getModuloReduction());
            stmt.setString(5, article.getMarque());
            stmt.setDouble(6, article.getQuantiteDispo());

            if (article.getImageStream() != null) {
                stmt.setBlob(7, article.getImageStream());
            } else {
                stmt.setNull(7, Types.BLOB);
            }

            stmt.setInt(8, article.getNote());
            stmt.setString(9, article.getDescription());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'insertion de l'article : " + e.getMessage());
            return false;
        }
    }

    // SELECT : récupérer un article par ID
    public Article findById(int id) {
        String sql = "SELECT * FROM Article WHERE id_article = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                InputStream imageStream = rs.getBinaryStream("image");
                byte[] imageBytes = rs.getBytes("image");

                Article article = new Article(
                        rs.getInt("id_article"),
                        rs.getString("nom_article"),
                        rs.getDouble("prix_unite"),
                        rs.getDouble("prix_vrac"),
                        rs.getInt("modulo_reduction"),
                        rs.getString("marque"),
                        rs.getDouble("quantite_dispo"),
                        imageStream,
                        rs.getInt("note"),
                        rs.getString("description")
                );

                article.setImageBytes(imageBytes); // ✅ important

                return article;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }

        return null;
    }

    // SELECT : récupérer tous les articles
    public List<Article> findAll() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM Article";

        try (Statement stmt = DBConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                InputStream imageStream = rs.getBinaryStream("image");
                byte[] imageBytes = rs.getBytes("image");

                Article article = new Article(
                        rs.getInt("id_article"),
                        rs.getString("nom_article"),
                        rs.getDouble("prix_unite"),
                        rs.getDouble("prix_vrac"),
                        rs.getInt("modulo_reduction"),
                        rs.getString("marque"),
                        rs.getDouble("quantite_dispo"),
                        imageStream,
                        rs.getInt("note"),
                        rs.getString("description")
                );

                article.setImageBytes(imageBytes); // ✅ pour affichage d’image

                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de tous les articles : " + e.getMessage());
        }

        return articles;
    }

    // UPDATE : modifier un article existant
    public boolean update(Article article) {
        String sql = "UPDATE Article SET nom_article = ?, prix_unite = ?, prix_vrac = ?, modulo_reduction = ?, " +
                "marque = ?, quantite_dispo = ?, image = ?, note = ?, description = ? WHERE id_article = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, article.getNomArticle());
            stmt.setDouble(2, article.getPrixUnite());
            stmt.setObject(3, article.getPrixVrac(), Types.DOUBLE);
            stmt.setInt(4, article.getModuloReduction());
            stmt.setString(5, article.getMarque());
            stmt.setDouble(6, article.getQuantiteDispo());

            if (article.getImageStream() != null) {
                stmt.setBlob(7, article.getImageStream());
            } else {
                stmt.setNull(7, Types.BLOB);
            }

            stmt.setInt(8, article.getNote());
            stmt.setString(9, article.getDescription());
            stmt.setInt(10, article.getIdArticle());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de l'article : " + e.getMessage());
            return false;
        }
    }

    // DELETE : supprimer un article
    public boolean delete(int idArticle) {
        String sql = "DELETE FROM Article WHERE id_article = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idArticle);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'article : " + e.getMessage());
            return false;
        }
    }
}

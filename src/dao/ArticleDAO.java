package dao;

import model.Article;
import utils.DBConnection;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour les articles.
 * Gère les opérations de lecture, insertion, mise à jour et suppression
 * dans la table Article de la base de données.
 */
public class ArticleDAO {

    /**
     * Insère un nouvel article dans la base de données.
     *
     * @param article L'article à ajouter.
     * @return true si l'insertion a réussi, false sinon.
     */
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
            System.err.println("Erreur lors de l'insertion de l'article : " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupère un article à partir de son identifiant.
     *
     * @param id L'identifiant de l'article.
     * @return L'article correspondant ou null si non trouvé.
     */
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

                article.setImageBytes(imageBytes);
                return article;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération : " + e.getMessage());
        }

        return null;
    }

    /**
     * Récupère tous les articles disponibles dans la base de données.
     *
     * @return Une liste d'objets Article.
     */
    public List<Article> findAll() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM Article";

        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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

                article.setImageBytes(imageBytes);
                articles.add(article);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les articles : " + e.getMessage());
        }

        return articles;
    }

    /**
     * Met à jour un article existant en changeant ses données enregistrées en SQL
     *
     * @param article L'article avec les nouvelles valeurs.
     * @return true si la mise à jour a réussi, false sinon.
     */
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
            System.err.println("Erreur lors de la mise à jour de l'article : " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime un article de la base ainsi que ses dépendances dans les autres tables.
     *
     * @param idArticle L'identifiant de l'article à supprimer.
     * @return true si la suppression a réussi, false sinon.
     */
    public boolean delete(int idArticle) {
        Connection conn = DBConnection.getConnection();

        try {
            String deletePanier = "DELETE FROM Panier WHERE id_article = ?";
            try (PreparedStatement stmtPanier = conn.prepareStatement(deletePanier)) {
                stmtPanier.setInt(1, idArticle);
                stmtPanier.executeUpdate();
            }

            String deleteLignes = "DELETE FROM Articles_Commandes WHERE id_article = ?";
            try (PreparedStatement stmtLignes = conn.prepareStatement(deleteLignes)) {
                stmtLignes.setInt(1, idArticle);
                stmtLignes.executeUpdate();
            }

            String sql = "DELETE FROM Article WHERE id_article = ?";
            try (PreparedStatement stmtArticle = conn.prepareStatement(sql)) {
                stmtArticle.setInt(1, idArticle);
                return stmtArticle.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'article : " + e.getMessage());
            return false;
        }
    }

    /**
     * Met à jour la note moyenne d’un article après un avis client.
     *
     * @param idArticle  L'identifiant de l'article concerné.
     * @param noteClient La note donnée par le client.
     * @return true si la mise à jour a réussi, false sinon.
     */
    public boolean ajouterNote(int idArticle, int noteClient) {
        String selectNoteSql = "SELECT note FROM Article WHERE id_article = ?";
        String countCommandeSql = "SELECT COUNT(DISTINCT id_commande) FROM articles_commandes WHERE id_article = ?";
        String updateNoteSql = "UPDATE Article SET note = ? WHERE id_article = ?";

        try (Connection conn = DBConnection.getConnection()) {
            int noteActuelle = 0;
            try (PreparedStatement stmt = conn.prepareStatement(selectNoteSql)) {
                stmt.setInt(1, idArticle);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    noteActuelle = rs.getInt("note");
                }
            }

            int nbCommandes = 0;
            try (PreparedStatement stmt = conn.prepareStatement(countCommandeSql)) {
                stmt.setInt(1, idArticle);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    nbCommandes = rs.getInt(1);
                }
            }

            int nouvelleNote = Math.round((noteActuelle * nbCommandes + noteClient) / (float) (nbCommandes + 1));

            try (PreparedStatement stmt = conn.prepareStatement(updateNoteSql)) {
                stmt.setInt(1, nouvelleNote);
                stmt.setInt(2, idArticle);
                return stmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la note : " + e.getMessage());
            return false;
        }
    }
}

package dao;

import model.Commande;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des commandes dans la base de données.
 * Permet la création, la recherche et la récupération des commandes.
 */
public class CommandeDAO {

    /**
     * Crée une nouvelle commande dans la base.
     *
     * @param commande La commande à insérer.
     * @return L'identifiant généré de la commande si succès, sinon -1.
     */
    public int create(Commande commande) {
        String sql = "INSERT INTO Commande (montant, date, id_user, note_client, statut, adresse_livraison, ville_livraison, code_postal) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, commande.getMontant());
            stmt.setDate(2, commande.getDate());
            stmt.setInt(3, commande.getIdUser());
            stmt.setObject(4, commande.getNoteClient(), Types.INTEGER);
            stmt.setString(5, commande.getStatut());
            stmt.setString(6, commande.getAdresseLivraison());
            stmt.setString(7, commande.getVilleLivraison());
            stmt.setString(8, commande.getCodePostal());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // id_commande généré
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la commande : " + e.getMessage());
        }

        return -1;
    }

    /**
     * Récupère une commande à partir de son identifiant.
     *
     * @param idCommande L'identifiant de la commande.
     * @return L'objet Commande correspondant, ou null si non trouvé.
     */
    public Commande findById(int idCommande) {
        String sql = "SELECT * FROM Commande WHERE id_commande = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idCommande);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Commande(
                        rs.getInt("id_commande"),
                        rs.getDouble("montant"),
                        rs.getDate("date"),
                        rs.getInt("id_user"),
                        rs.getObject("note_client") != null ? rs.getInt("note_client") : null,
                        rs.getString("statut"),
                        rs.getString("adresse_livraison"),
                        rs.getString("ville_livraison"),
                        rs.getString("code_postal")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la commande : " + e.getMessage());
        }

        return null;
    }

    /**
     * Récupère toutes les commandes associées à un utilisateur.
     *
     * @param idUser L'identifiant utilisateur.
     * @return Une liste de commandes liées à l'utilisateur.
     */
    public List<Commande> findByUser(int idUser) {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM Commande WHERE id_user = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Commande c = new Commande(
                        rs.getInt("id_commande"),
                        rs.getDouble("montant"),
                        rs.getDate("date"),
                        rs.getInt("id_user"),
                        rs.getObject("note_client") != null ? rs.getInt("note_client") : null,
                        rs.getString("statut"),
                        rs.getString("adresse_livraison"),
                        rs.getString("ville_livraison"),
                        rs.getString("code_postal")
                );
                commandes.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commandes utilisateur : " + e.getMessage());
        }

        return commandes;
    }

    /**
     * Récupère toutes les commandes de la base de données.
     *
     * @return Une liste contenant toutes les commandes.
     */
    public List<Commande> findAll() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM Commande";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Commande c = new Commande(
                        rs.getInt("id_commande"),
                        rs.getDouble("montant"),
                        rs.getDate("date"),
                        rs.getInt("id_user"),
                        rs.getObject("note_client") != null ? rs.getInt("note_client") : null,
                        rs.getString("statut"),
                        rs.getString("adresse_livraison"),
                        rs.getString("ville_livraison"),
                        rs.getString("code_postal")
                );
                commandes.add(c);
            }

        } catch (SQLException e) {
            System.err.println("Erreur récupération de toutes les commandes : " + e.getMessage());
        }

        return commandes;
    }
}

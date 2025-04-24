package dao;

import model.Commande;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {

    // Créer une commande
    public int create(Commande commande) {
        String sql = "INSERT INTO Commande (montant, date, id_user, note_client, statut) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, commande.getMontant());
            stmt.setDate(2, commande.getDate());
            stmt.setInt(3, commande.getIdUser());
            stmt.setObject(4, commande.getNoteClient(), Types.INTEGER); // accepte null
            stmt.setString(5, commande.getStatut());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // id_commande généré
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la création de la commande : " + e.getMessage());
        }

        return -1;
    }

    // Récupérer une commande par son ID
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
                        rs.getString("statut")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de la commande : " + e.getMessage());
        }

        return null;
    }

    // Récupérer toutes les commandes d’un utilisateur
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
                        rs.getString("statut")
                );
                commandes.add(c);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des commandes utilisateur : " + e.getMessage());
        }

        return commandes;
    }

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
                        rs.getString("statut")
                );
                commandes.add(c);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération de toutes les commandes : " + e.getMessage());
        }

        return commandes;
    }

}

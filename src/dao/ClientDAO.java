package dao;

import model.Client;
import model.Compte;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO pour la table Client.
 * Permet la création, la récupération et la mise à jour des informations clients.
 */
public class ClientDAO {

    /**
     * Insère un nouveau client dans la base de données.
     *
     * @param client Objet Client contenant les données à insérer.
     * @return true si l'insertion réussit, false sinon.
     */
    public boolean create(Client client) {
        String sql = "INSERT INTO Client (id_user, nom, prenom) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, client.getIdUser());
            stmt.setString(2, client.getNom());
            stmt.setString(3, client.getPrenom());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du client : " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupère un client à partir de l'identifiant utilisateur.
     *
     * @param idUser Identifiant de l'utilisateur.
     * @return Un objet Client si trouvé, null sinon.
     */
    public Client findClientByIdUser(int idUser) {
        String sqlCompte = "SELECT * FROM Compte WHERE id_user = ?";
        String sqlClient = "SELECT nom, prenom FROM Client WHERE id_user = ?";

        try {
            Connection conn = DBConnection.getConnection();

            // Recherche dans Compte
            PreparedStatement stmtCompte = conn.prepareStatement(sqlCompte);
            stmtCompte.setInt(1, idUser);
            ResultSet rsC = stmtCompte.executeQuery();

            if (!rsC.next()) return null;

            Compte compte = new Compte(
                    rsC.getInt("id_user"),
                    rsC.getString("login"),
                    rsC.getString("mdp"),
                    rsC.getBoolean("admin")
            );

            // Recherche dans Client
            PreparedStatement stmtClient = conn.prepareStatement(sqlClient);
            stmtClient.setInt(1, idUser);
            ResultSet rsCli = stmtClient.executeQuery();

            if (rsCli.next()) {
                return new Client(compte, rsCli.getString("nom"), rsCli.getString("prenom"));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du client : " + e.getMessage());
        }

        return null;
    }

    /**
     * Met à jour les informations personnelles du client.
     * Met à jour le mot de passe uniquement si un nouveau mot de passe est fourni.
     *
     * @param client Objet Client à mettre à jour.
     * @return true si la mise à jour réussit, false sinon.
     */
    public boolean update(Client client) {
        String sqlClient = "UPDATE Client SET nom = ?, prenom = ? WHERE id_user = ?";
        String sqlCompte = "UPDATE Compte SET mdp = ? WHERE id_user = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // Mise à jour nom/prénom
            try (PreparedStatement stmtClient = conn.prepareStatement(sqlClient)) {
                stmtClient.setString(1, client.getNom());
                stmtClient.setString(2, client.getPrenom());
                stmtClient.setInt(3, client.getIdUser());
                stmtClient.executeUpdate();
            }

            // Mise à jour mot de passe si fourni
            if (client.getMdp() != null && !client.getMdp().isBlank()) {
                try (PreparedStatement stmtCompte = conn.prepareStatement(sqlCompte)) {
                    stmtCompte.setString(1, client.getMdp());
                    stmtCompte.setInt(2, client.getIdUser());
                    stmtCompte.executeUpdate();
                }
            }

            return true;

        } catch (SQLException e) {
            System.err.println("Erreur mise à jour client : " + e.getMessage());
            return false;
        }
    }
}

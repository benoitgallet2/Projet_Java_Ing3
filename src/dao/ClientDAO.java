package dao;

import model.Client;
import model.Compte;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientDAO {

    public boolean create(Client client) {
        String sql = "INSERT INTO Client (id_user, nom, prenom) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, client.getIdUser());
            stmt.setString(2, client.getNom());
            stmt.setString(3, client.getPrenom());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la création du client : " + e.getMessage());
            return false;
        }
    }

    public boolean update(Client client) {
        String sqlClient = "UPDATE Client SET nom = ?, prenom = ? WHERE id_user = ?";
        String sqlCompte = "UPDATE Compte SET mdp = ? WHERE id_user = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // Mettre à jour nom & prénom
            try (PreparedStatement stmtClient = conn.prepareStatement(sqlClient)) {
                stmtClient.setString(1, client.getNom());
                stmtClient.setString(2, client.getPrenom());
                stmtClient.setInt(3, client.getIdUser());
                stmtClient.executeUpdate();
            }

            // Mettre à jour mot de passe si nécessaire
            if (client.getMdp() != null && !client.getMdp().isBlank()) {
                try (PreparedStatement stmtCompte = conn.prepareStatement(sqlCompte)) {
                    stmtCompte.setString(1, client.getMdp());
                    stmtCompte.setInt(2, client.getIdUser());
                    stmtCompte.executeUpdate();
                }
            }

            return true;

        } catch (SQLException e) {
            System.err.println("❌ Erreur mise à jour client : " + e.getMessage());
            return false;
        }
    }

    public Client findClientByIdUser(int idUser) {
        String sqlCompte = "SELECT * FROM Compte WHERE id_user = ?";
        String sqlClient = "SELECT nom, prenom FROM Client WHERE id_user = ?";

        try {
            Connection conn = DBConnection.getConnection();

            // Requête sur Compte
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

            // Requête sur Client
            PreparedStatement stmtClient = conn.prepareStatement(sqlClient);
            stmtClient.setInt(1, idUser);
            ResultSet rsCli = stmtClient.executeQuery();

            if (rsCli.next()) {
                return new Client(compte, rsCli.getString("nom"), rsCli.getString("prenom"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du client : " + e.getMessage());
        }

        return null;
    }
}

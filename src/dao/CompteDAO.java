package dao;

import model.Compte;
import utils.DBConnection;

import java.sql.*;

public class CompteDAO {

    // Créer un compte (retourne l'ID généré ou -1 si erreur)
    public int create(Compte compte) {
        String sql = "INSERT INTO Compte (login, mdp, admin) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, compte.getLogin());
            stmt.setString(2, compte.getMdp());
            stmt.setBoolean(3, compte.isAdmin());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // retourne l'id_user généré
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la création du compte : " + e.getMessage());
        }

        return -1;
    }

    // Récupérer un compte par login
    public Compte findByLogin(String login) {
        String sql = "SELECT * FROM Compte WHERE login = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Compte(
                        rs.getInt("id_user"),
                        rs.getString("login"),
                        rs.getString("mdp"),
                        rs.getBoolean("admin")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du compte : " + e.getMessage());
        }

        return null;
    }

}

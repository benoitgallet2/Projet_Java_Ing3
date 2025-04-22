package model;

public class Compte {
    private int idUser;
    private String login;
    private String mdp;
    private boolean admin;

    // Constructeur sans ID (pour l'insertion)
    public Compte(String login, String mdp, boolean admin) {
        this.login = login;
        this.mdp = mdp;
        this.admin = admin;
    }

    // Constructeur complet (récupération depuis la base)
    public Compte(int idUser, String login, String mdp, boolean admin) {
        this(login, mdp, admin);
        this.idUser = idUser;
    }

    // Getters & Setters
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getMdp() { return mdp; }
    public void setMdp(String mdp) { this.mdp = mdp; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }

    @Override
    public String toString() {
        return "Compte{" +
                "idUser=" + idUser +
                ", login='" + login + '\'' +
                ", admin=" + admin +
                '}';
    }
}

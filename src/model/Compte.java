package model;

/**
 * Représente un compte utilisateur, qui peut être un client ou un administrateur.
 */
public class Compte {

    private int idUser;
    private String login;
    private String mdp;
    private boolean admin;

    /**
     * Constructeur utilisé lors de la création d'un compte (sans ID).
     *
     * @param login identifiant de connexion
     * @param mdp mot de passe
     * @param admin indique si le compte est administrateur
     */
    public Compte(String login, String mdp, boolean admin) {
        this.login = login;
        this.mdp = mdp;
        this.admin = admin;
    }

    /**
     * Constructeur complet (utilisé pour les données récupérées de la base).
     *
     * @param idUser identifiant utilisateur
     * @param login identifiant de connexion
     * @param mdp mot de passe
     * @param admin statut administrateur
     */
    public Compte(int idUser, String login, String mdp, boolean admin) {
        this(login, mdp, admin);
        this.idUser = idUser;
    }

    /**
     * Retourne l'identifiant utilisateur.
     *
     * @return idUser
     */
    public int getIdUser() {
        return idUser;
    }

    /**
     * Modifie l'identifiant utilisateur.
     *
     * @param idUser nouvel identifiant
     */
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    /**
     * Retourne le login du compte.
     *
     * @return login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Modifie le login du compte.
     *
     * @param login nouveau login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Retourne le mot de passe.
     *
     * @return mot de passe
     */
    public String getMdp() {
        return mdp;
    }

    /**
     * Modifie le mot de passe.
     *
     * @param mdp nouveau mot de passe
     */
    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    /**
     * Vérifie si le compte est administrateur.
     *
     * @return true si admin, false sinon
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Modifie le statut administrateur.
     *
     * @param admin nouveau statut
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "Compte{" +
                "idUser=" + idUser +
                ", login='" + login + '\'' +
                ", admin=" + admin +
                '}';
    }
}

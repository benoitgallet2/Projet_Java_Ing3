package model;

/**
 * Représente un client, qui est un utilisateur avec un compte (hérite de Compte),
 * et qui possède des informations personnelles comme son nom et prénom.
 */
public class Client extends Compte {

    private String nom;
    private String prenom;

    /**
     * Constructeur complet du client.
     *
     * @param idUser identifiant utilisateur
     * @param login identifiant de connexion
     * @param mdp mot de passe
     * @param admin si l'utilisateur est administrateur
     * @param nom nom du client
     * @param prenom prénom du client
     */
    public Client(int idUser, String login, String mdp, boolean admin, String nom, String prenom) {
        super(idUser, login, mdp, admin);
        this.nom = nom;
        this.prenom = prenom;
    }

    /**
     * Constructeur à partir d'un objet Compte existant.
     *
     * @param compte compte de base
     * @param nom nom du client
     * @param prenom prénom du client
     */
    public Client(Compte compte, String nom, String prenom) {
        super(compte.getIdUser(), compte.getLogin(), compte.getMdp(), compte.isAdmin());
        this.nom = nom;
        this.prenom = prenom;
    }

    /**
     * Retourne le nom du client.
     *
     * @return nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom du client.
     *
     * @param nom nom à définir
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Retourne le prénom du client.
     *
     * @return prénom
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Définit le prénom du client.
     *
     * @param prenom prénom à définir
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @Override
    public String toString() {
        return super.toString() + " | Client{" +
                "nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                '}';
    }
}

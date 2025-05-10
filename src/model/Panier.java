package model;

/**
 * Représente une ligne de panier : un article ajouté par un utilisateur.
 */
public class Panier {

    private int idUser;
    private int idArticle;

    /**
     * Constructeur de Panier.
     *
     * @param idUser identifiant de l'utilisateur
     * @param idArticle identifiant de l'article
     */
    public Panier(int idUser, int idArticle) {
        this.idUser = idUser;
        this.idArticle = idArticle;
    }

    /**
     * Retourne l'identifiant de l'utilisateur.
     *
     * @return idUser
     */
    public int getIdUser() {
        return idUser;
    }

    /**
     * Modifie l'identifiant de l'utilisateur.
     *
     * @param idUser nouvel identifiant utilisateur
     */
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    /**
     * Retourne l'identifiant de l'article.
     *
     * @return idArticle
     */
    public int getIdArticle() {
        return idArticle;
    }

    /**
     * Modifie l'identifiant de l'article.
     *
     * @param idArticle nouvel identifiant article
     */
    public void setIdArticle(int idArticle) {
        this.idArticle = idArticle;
    }

    /**
     * Représentation textuelle de l'objet Panier.
     *
     * @return chaîne de caractères décrivant le panier
     */
    @Override
    public String toString() {
        return "Panier{" +
                "idUser=" + idUser +
                ", idArticle=" + idArticle +
                '}';
    }
}

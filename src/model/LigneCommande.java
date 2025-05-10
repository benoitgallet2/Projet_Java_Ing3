package model;

/**
 * Représente une ligne d'une commande, associant un article à une commande.
 */
public class LigneCommande {

    private int idCommande;
    private int idArticle;

    /**
     * Constructeur de la ligne de commande.
     *
     * @param idCommande identifiant de la commande
     * @param idArticle identifiant de l'article
     */
    public LigneCommande(int idCommande, int idArticle) {
        this.idCommande = idCommande;
        this.idArticle = idArticle;
    }

    /**
     * Retourne l'identifiant de la commande.
     *
     * @return idCommande
     */
    public int getIdCommande() {
        return idCommande;
    }

    /**
     * Retourne l'identifiant de l'article.
     *
     * @return idArticle
     */
    public int getIdArticle() {
        return idArticle;
    }
}

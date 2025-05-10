package model;

/**
 * Représente une ligne dans la table Articles_Commandes (relation entre une commande et un article).
 * Cette classe fait le lien entre un article et une commande spécifique.
 */
public class ArticlesCommandes {

    private int idCommande;
    private int idArticle;

    /**
     * Constructeur principal.
     *
     * @param idCommande identifiant de la commande
     * @param idArticle identifiant de l'article
     */
    public ArticlesCommandes(int idCommande, int idArticle) {
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
     * Définit l'identifiant de la commande.
     *
     * @param idCommande identifiant de la commande
     */
    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
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
     * Définit l'identifiant de l'article.
     *
     * @param idArticle identifiant de l'article
     */
    public void setIdArticle(int idArticle) {
        this.idArticle = idArticle;
    }

    @Override
    public String toString() {
        return "ArticlesCommandes{" +
                "idCommande=" + idCommande +
                ", idArticle=" + idArticle +
                '}';
    }
}

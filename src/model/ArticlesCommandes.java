package model;

public class ArticlesCommandes {
    private int idCommande;
    private int idArticle;

    public ArticlesCommandes(int idCommande, int idArticle) {
        this.idCommande = idCommande;
        this.idArticle = idArticle;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public int getIdArticle() {
        return idArticle;
    }

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

package model;

public class LigneCommande {
    private int idCommande;
    private int idArticle;

    public LigneCommande(int idCommande, int idArticle) {
        this.idCommande = idCommande;
        this.idArticle = idArticle;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public int getIdArticle() {
        return idArticle;
    }
}

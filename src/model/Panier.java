package model;

public class Panier {
    private int idUser;
    private int idArticle;

    public Panier(int idUser, int idArticle) {
        this.idUser = idUser;
        this.idArticle = idArticle;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(int idArticle) {
        this.idArticle = idArticle;
    }

    @Override
    public String toString() {
        return "Panier{" +
                "idUser=" + idUser +
                ", idArticle=" + idArticle +
                '}';
    }
}

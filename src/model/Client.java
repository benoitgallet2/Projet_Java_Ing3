package model;

public class Client extends Compte {
    private String nom;
    private String prenom;

    public Client(int idUser, String login, String mdp, boolean admin, String nom, String prenom) {
        super(idUser, login, mdp, admin);
        this.nom = nom;
        this.prenom = prenom;
    }

    public Client(Compte compte, String nom, String prenom) {
        super(compte.getIdUser(), compte.getLogin(), compte.getMdp(), compte.isAdmin());
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    @Override
    public String toString() {
        return super.toString() + " | Client{" +
                "nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                '}';
    }
}

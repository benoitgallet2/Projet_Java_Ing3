package model;

import java.sql.Date;

/**
 * Représente une commande passée par un utilisateur.
 */
public class Commande {

    private int idCommande;
    private double montant;
    private Date date;
    private int idUser;
    private Integer noteClient;
    private String statut;

    private String adresseLivraison;
    private String villeLivraison;
    private String codePostal;

    /**
     * Constructeur utilisé pour insérer une commande (sans ID).
     *
     * @param montant montant total de la commande
     * @param date date de la commande
     * @param idUser identifiant de l'utilisateur ayant passé la commande
     * @param noteClient note donnée par le client (peut être null)
     * @param statut statut de la commande (ex : "Payé", "Livré")
     * @param adresseLivraison adresse de livraison
     * @param villeLivraison ville de livraison
     * @param codePostal code postal de livraison
     */
    public Commande(double montant, Date date, int idUser, Integer noteClient, String statut,
                    String adresseLivraison, String villeLivraison, String codePostal) {
        this.montant = montant;
        this.date = date;
        this.idUser = idUser;
        this.noteClient = noteClient;
        this.statut = statut;
        this.adresseLivraison = adresseLivraison;
        this.villeLivraison = villeLivraison;
        this.codePostal = codePostal;
    }

    /**
     * Constructeur complet (utilisé pour récupérer depuis la base).
     *
     * @param idCommande identifiant de la commande
     * @param montant montant total
     * @param date date
     * @param idUser identifiant utilisateur
     * @param noteClient note du client
     * @param statut statut (ex : "Payé", "Livré")
     * @param adresseLivraison adresse
     * @param villeLivraison ville
     * @param codePostal code postal
     */
    public Commande(int idCommande, double montant, Date date, int idUser, Integer noteClient, String statut,
                    String adresseLivraison, String villeLivraison, String codePostal) {
        this(montant, date, idUser, noteClient, statut, adresseLivraison, villeLivraison, codePostal);
        this.idCommande = idCommande;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public Integer getNoteClient() {
        return noteClient;
    }

    public void setNoteClient(Integer noteClient) {
        this.noteClient = noteClient;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getAdresseLivraison() {
        return adresseLivraison;
    }

    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    public String getVilleLivraison() {
        return villeLivraison;
    }

    public void setVilleLivraison(String villeLivraison) {
        this.villeLivraison = villeLivraison;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "idCommande=" + idCommande +
                ", montant=" + montant +
                ", date=" + date +
                ", idUser=" + idUser +
                ", noteClient=" + noteClient +
                ", statut='" + statut + '\'' +
                ", adresseLivraison='" + adresseLivraison + '\'' +
                ", villeLivraison='" + villeLivraison + '\'' +
                ", codePostal='" + codePostal + '\'' +
                '}';
    }
}

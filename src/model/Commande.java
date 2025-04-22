package model;

import java.sql.Date;

public class Commande {
    private int idCommande;
    private double montant;
    private Date date;
    private int idUser;
    private Integer noteClient; // nullable
    private String statut; // "Payé" ou "Livré"

    // Constructeur sans ID (insertion)
    public Commande(double montant, Date date, int idUser, Integer noteClient, String statut) {
        this.montant = montant;
        this.date = date;
        this.idUser = idUser;
        this.noteClient = noteClient;
        this.statut = statut;
    }

    // Constructeur complet (lecture BDD)
    public Commande(int idCommande, double montant, Date date, int idUser, Integer noteClient, String statut) {
        this(montant, date, idUser, noteClient, statut);
        this.idCommande = idCommande;
    }

    // Getters & Setters
    public int getIdCommande() { return idCommande; }
    public void setIdCommande(int idCommande) { this.idCommande = idCommande; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public Integer getNoteClient() { return noteClient; }
    public void setNoteClient(Integer noteClient) { this.noteClient = noteClient; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "Commande{" +
                "idCommande=" + idCommande +
                ", montant=" + montant +
                ", date=" + date +
                ", idUser=" + idUser +
                ", noteClient=" + noteClient +
                ", statut='" + statut + '\'' +
                '}';
    }
}

package model;

public class Article {
    private int idArticle;
    private String nomArticle;
    private double prixUnite;
    private Double prixVrac;
    private int moduloReduction;
    private String marque;
    private double quantiteDispo;
    private byte[] image;
    private int note;
    private String description;

    // Constructeur sans ID (utilisé avant insertion en base)
    public Article(String nomArticle, double prixUnite, Double prixVrac, int moduloReduction,
                   String marque, double quantiteDispo, byte[] image, int note, String description) {
        this.nomArticle = nomArticle;
        this.prixUnite = prixUnite;
        this.prixVrac = prixVrac;
        this.moduloReduction = moduloReduction;
        this.marque = marque;
        this.quantiteDispo = quantiteDispo;
        this.image = image;
        this.note = note;
        this.description = description;
    }

    // Constructeur complet (utilisé pour récupération en base)
    public Article(int idArticle, String nomArticle, double prixUnite, Double prixVrac, int moduloReduction,
                   String marque, double quantiteDispo, byte[] image, int note, String description) {
        this(nomArticle, prixUnite, prixVrac, moduloReduction, marque, quantiteDispo, image, note, description);
        this.idArticle = idArticle;
    }

    // Getters & Setters
    public int getIdArticle() { return idArticle; }
    public void setIdArticle(int idArticle) { this.idArticle = idArticle; }

    public String getNomArticle() { return nomArticle; }
    public void setNomArticle(String nomArticle) { this.nomArticle = nomArticle; }

    public double getPrixUnite() { return prixUnite; }
    public void setPrixUnite(double prixUnite) { this.prixUnite = prixUnite; }

    public Double getPrixVrac() { return prixVrac; }
    public void setPrixVrac(Double prixVrac) { this.prixVrac = prixVrac; }

    public int getModuloReduction() { return moduloReduction; }
    public void setModuloReduction(int moduloReduction) { this.moduloReduction = moduloReduction; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public double getQuantiteDispo() { return quantiteDispo; }
    public void setQuantiteDispo(double quantiteDispo) { this.quantiteDispo = quantiteDispo; }

    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }

    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Article{" +
                "idArticle=" + idArticle +
                ", nomArticle='" + nomArticle + '\'' +
                ", prixUnite=" + prixUnite +
                ", prixVrac=" + prixVrac +
                ", moduloReduction=" + moduloReduction +
                ", marque='" + marque + '\'' +
                ", quantiteDispo=" + quantiteDispo +
                ", note=" + note +
                ", description='" + description + '\'' +
                '}';
    }
}

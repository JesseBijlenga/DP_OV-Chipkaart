package domain;


import java.util.ArrayList;
import java.util.List;

public class Product {

    private int product_nummer;
    private List<Integer> ovChipkaarten = new ArrayList<>();
    private String naam;
    private String beschrijving;
    private double prijs;

    public Product(int product_nummer, String naam, String beschrijving, double prijs) {
        this.product_nummer = product_nummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
    }

    public int getProduct_nummer() {
        return product_nummer;
    }

    public void setProduct_nummer(int product_nummer) {
        this.product_nummer = product_nummer;
    }

    public List<Integer> getOvChipkaarten() {
        return ovChipkaarten;
    }

    public void setOvChipkaarten(List<Integer> ovChipkaarten) {
        this.ovChipkaarten = ovChipkaarten;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public double getPrijs() {
        return prijs;
    }

    public void setPrijs(double prijs) {
        this.prijs = prijs;
    }

    public void addOv(int ovChipkaart){
        ovChipkaarten.add(ovChipkaart);
    }

    public void removeOv(OVChipkaart ovChipkaart){
        ovChipkaarten.remove(ovChipkaart);
    }

    @Override
    public String toString() {
        String ovChipkaartenStr = ovChipkaarten.isEmpty() ? "[]" : ovChipkaarten.toString();

        return "Product [" +
                "product_nummer=" + product_nummer +
                ", naam='" + naam + '\'' +
                ", beschrijving='" + beschrijving + '\'' +
                ", prijs=" + prijs +
                ", ovChipkaarten=" + ovChipkaartenStr +
                ']';
    }

}


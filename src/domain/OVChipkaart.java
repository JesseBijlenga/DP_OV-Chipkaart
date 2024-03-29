package domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaart {
    private int kaart_nummer;
    private LocalDate geldig_tot;
    private int klasse;
    private double saldo;
    private Reiziger reiziger;
    private List<Product> producten = new ArrayList<>();

    public OVChipkaart(int kaart_nummer, LocalDate geldig_tot, int klasse, double saldo, Reiziger reiziger) {
        this.kaart_nummer = kaart_nummer;
        this.geldig_tot = geldig_tot;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger = reiziger;

    }

    public List<Product> getProducten() {
        return producten;
    }

    public void setProducten(List<Product> producten) {
        this.producten = producten;
    }
    public boolean addProduct(Product product){
        product.addOv(this.getKaart_nummer());
        producten.add(product);
        return true;
    }

    public boolean removeProduct(Product product){
        product.removeOv(this);
        producten.remove(product);
        return true;
    }

    public int getKaart_nummer() {
        return kaart_nummer;
    }

    public void setKaart_nummer(int kaart_nummer) {
        this.kaart_nummer = kaart_nummer;
    }

    public LocalDate getGeldig_tot() {
        return geldig_tot;
    }

    public void setGeldig_tot(LocalDate geldig_tot) {
        this.geldig_tot = geldig_tot;
    }

    public int getKlasse() {
        return klasse;
    }

    public void setKlasse(int klasse) {
        this.klasse = klasse;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public Reiziger getReiziger() {
        return reiziger;
    }

    public void setReiziger(Reiziger reiziger) {
        this.reiziger = reiziger;
    }
    public String toString() {
        StringBuilder productsString = new StringBuilder("[");
        for (Product product : producten) {
            productsString.append(product.toString()).append(", ");
        }
        if (!producten.isEmpty()) {
            productsString.setLength(productsString.length() - 2);
        }
        productsString.append("]");
        return String.format("OV: {nummer = %d, geldig tot = %s, klasse = %d, saldo = %.2f, reiziger_id = %d, producten = %s}",
                this.kaart_nummer,
                this.geldig_tot,
                this.klasse,
                this.saldo,
                this.reiziger.getId(),
                productsString);
    }
}

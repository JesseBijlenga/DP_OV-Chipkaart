package domain;

import java.time.LocalDate;

public class Reiziger {
    private int id;
    private String voorletter;
    private String tussenvoegsel;
    private String achternaam;
    private LocalDate gbDatum;

    private Adres adres;



    public Reiziger(int id, String voorletter, String tussenvoegsel, String achternaam, LocalDate gbDatum) {
        this.id = id;
        this.voorletter = voorletter;
        this.tussenvoegsel = tussenvoegsel;
        this.achternaam = achternaam;
        this.gbDatum = gbDatum;
    }
    public int getId() {
        return id;
    }
    public Adres getAdres() {
        return adres;
    }

    public void setAdres(Adres adres) {
        this.adres = adres;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVoorletter() {
        return voorletter;
    }

    public void setVoorletter(String voorletter) {
        this.voorletter = voorletter;
    }

    public String getTussenvoegsel() {
        return tussenvoegsel;
    }

    public void setTussenvoegsel(String tussenvoegsel) {
        this.tussenvoegsel = tussenvoegsel;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }

    public LocalDate getGbDatum() {
        return gbDatum;
    }

    public void setGbDatum(LocalDate gbDatum) {
        this.gbDatum = gbDatum;
    }
    public String toString(){
        return String.format("#%d: %s. %s %s (%s)", this.id, this.voorletter, this.tussenvoegsel != null ? this.tussenvoegsel : "\b", this.achternaam, this.gbDatum);
    }

}
import application.AdresDAOsql;
import application.ReizigerDAOsql;
import data.AdresDAO;
import data.ReizigerDAO;
import domain.Adres;
import domain.Reiziger;

import java.sql.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        String dbUrl = "jdbc:postgresql://localhost:5432/ovchip";
        String user = "postgres";
        String pass = "123";

        Connection conn = DriverManager.getConnection(dbUrl, user, pass);
        ReizigerDAOsql doa = new ReizigerDAOsql(conn);
        AdresDAOsql doa2 = new AdresDAOsql(conn);
        testAdresDAO(doa2, doa);
        //testReizigerDAO(doa);
}
    /**
     * P2. Reiziger DAO: persistentie van een klasse
     *
     * Deze methode test de CRUD-functionaliteit van de Reiziger DAO
     *
     * @throws SQLException
     */
    private static void testReizigerDAO(ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        // Haal alle reizigers op uit de database
        List<Reiziger> reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        // Maak een nieuwe reiziger aan en persisteer deze in de database
        String gbdatum = "1981-03-14";
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", Date.valueOf(gbdatum).toLocalDate());
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");
        System.out.println(rdao.findById(77));
        // Voeg aanvullende tests van de ontbrekende CRUD-operaties in.

        // Test het bijwerken van een reiziger
        System.out.println("[Test] ReizigerDAO.update() - Update reiziger:");

        sietske.setVoorletter("S C");

        rdao.update(sietske);
        Reiziger gewijzigdeSietske = rdao.findById(sietske.getId());
        System.out.println("[Test] Gewijzigde reiziger: " + gewijzigdeSietske);

        // Test het verwijderen van een reiziger
        System.out.println("[Test] ReizigerDAO.delete() - Verwijder reiziger:");
        rdao.delete(sietske);
        Reiziger verwijderdeSietske = rdao.findById(sietske.getId());
        System.out.println("[Test] Verwijderde reiziger (null als verwijderd): " + verwijderdeSietske);
    }
    private static void testAdresDAO(AdresDAO adao, ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test AdresDAO -------------");
        Reiziger r = new Reiziger(77, "S", "", "Boers", Date.valueOf("1981-03-14").toLocalDate());
        rdao.save(r);
        Adres ad = new Adres(6, "3401 VB", "38", "Wijkstraat", "IJsselstein", r);
        r.setAdres(ad);
        adao.save(ad);
        // Haal alle adressen op uit de database
        List<Adres> adressen = adao.findAll();
        System.out.println("[Test] AdresDAO.findAll() geeft de volgende adressen:");
        for (Adres a : adressen) {
            System.out.println(a);
        }
        System.out.println();

    }
}
package application;

import data.ProductDAO;
import domain.OVChipkaart;
import domain.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDAOsql implements ProductDAO {
    private final Connection conn;

    public ProductDAOsql(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Product> findAll(){
        try {
            PreparedStatement ps = conn.prepareStatement("""
                SELECT
                p.product_nummer,
                p.prijs,
                p.beschrijving,
                p.naam
                FROM product p
                ORDER BY product_nummer;
                """);
            ResultSet rs = ps.executeQuery();
            List<Product> producten = new ArrayList<Product>();
            while (rs.next()){
                Product product = new Product(rs.getInt("product_nummer"), rs.getString("naam"), rs.getString("beschrijving"), rs.getDouble("prijs"));
                PreparedStatement ps2 = conn.prepareStatement("""
                        SELECT 
                        ocp.kaart_nummer
                        FROM 
                        ov_chipkaart_product ocp
                        WHERE product_nummer = ?;
                        """);
                ps2.setInt(1, product.getProduct_nummer());
                ResultSet rsp = ps2.executeQuery();
                while(rsp.next()){
                    product.addOv(rsp.getInt("kaart_nummer"));
                }
                producten.add(product);
            }
            ps.close();
            return producten;
        }catch (SQLException e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean updateLink(Product product) {
        try {
            List<Integer> nummers = product.getOvChipkaarten();
            String sqlString = "-1";

            if (nummers.size() != 0) {
                sqlString = nummers.stream().map(v -> "?").collect(Collectors.joining(", "));
            }

            try (PreparedStatement prs = conn.prepareStatement(
                    "DELETE FROM ov_chipkaart_product ocp " +
                            "WHERE ocp.product_nummer = ? " +
                            "AND ocp.kaart_nummer NOT IN (" + sqlString + ");")) {

                prs.setInt(1, product.getProduct_nummer());

                for (int i = 1; i < nummers.size() + 1; i++) {
                    prs.setInt(i + 1, nummers.get(i - 1));
                }

                prs.execute();
            }

            for (Integer ovChipkaart : product.getOvChipkaarten()) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO ov_chipkaart_product(kaart_nummer, product_nummer) " +
                                "VALUES(?, ?) ON CONFLICT DO NOTHING;")) {

                    ps.setInt(1, ovChipkaart);
                    ps.setInt(2, product.getProduct_nummer());
                    ps.execute();
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }


    @Override
    public boolean save(Product product) {
        String sql = "INSERT INTO product (product_nummer, naam, beschrijving, prijs) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getProduct_nummer());
            ps.setString(2, product.getNaam());
            ps.setString(3, product.getBeschrijving());
            ps.setDouble(4, product.getPrijs());
            ps.execute();
            ps.close();
            updateLink(product);
            for (Integer ovChipkaart : product.getOvChipkaarten()) {
                linkProductOV(ovChipkaart, product);
            }

            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Product product) {
        String sql = "UPDATE product SET naam = ?, beschrijving = ?, prijs = ? WHERE product_nummer = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getNaam());
            ps.setString(2, product.getBeschrijving());
            ps.setDouble(3, product.getPrijs());
            ps.setInt(4, product.getProduct_nummer());
            ps.execute();
            ps.close();

            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Product product) {
        for(Integer ovChipkaart : product.getOvChipkaarten()){
            unlinkProductOV(ovChipkaart, product);
        }
        String sql = "DELETE FROM product WHERE product_nummer = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getProduct_nummer());
            ps.execute();
            ps.close();

            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean unlinkProductOV(int ovChipkaart, Product product) {
        try {
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM ov_chipkaart_product WHERE kaart_nummer = ? AND product_nummer = ?")) {

                ps.setInt(1, ovChipkaart);
                ps.setInt(2, product.getProduct_nummer());
                ps.execute();
            }
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT " +
                            "p.product_nummer, " +
                            "p.prijs, " +
                            "p.beschrijving, " +
                            "p.naam " +
                            "FROM product p " +
                            "JOIN ov_chipkaart_product ocp " +
                            "ON p.product_nummer = ocp.product_nummer " +
                            "AND ocp.kaart_nummer = ?"
            );
            ps.setInt(1, ovChipkaart.getKaart_nummer());
            ResultSet rs = ps.executeQuery();
            List<Product> producten = new ArrayList<Product>();
            while (rs.next()) {
                Product product = new Product(rs.getInt("product_nummer"), rs.getString("naam"), rs.getString("beschrijving"), rs.getDouble("prijs"));

                PreparedStatement ps2 = conn.prepareStatement(
                        "SELECT " +
                                "ocp.kaart_nummer " +
                                "FROM " +
                                "ov_chipkaart_product ocp " +
                                "WHERE product_nummer = ?"
                );
                ps2.setInt(1, product.getProduct_nummer());
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    product.addOv(rs2.getInt("kaart_nummer"));
                }
                producten.add(product);
            }
            ps.close();
            return producten;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }


    @Override
    public boolean linkProductOV(int ovChipkaart, Product product) {
        String sql = "INSERT INTO ov_chipkaart_product (kaart_nummer, product_nummer) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ovChipkaart);
            ps.setInt(2, product.getProduct_nummer());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}

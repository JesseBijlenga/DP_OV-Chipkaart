package application;

import data.OVChipkaartDAO;
import data.ReizigerDAO;
import domain.Adres;
import domain.OVChipkaart;
import domain.Product;
import domain.Reiziger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOsql implements OVChipkaartDAO {
    private final Connection conn;
    private ReizigerDAO rdao;
    private ProductDAOsql productDAOsql;

    public void setProductDAOsql(ProductDAOsql productDAOsql) {
        this.productDAOsql = productDAOsql;
    }

    public OVChipkaartDAOsql(Connection conn, ReizigerDAO rdao) {
        this.conn = conn;
        this.rdao = rdao;
    }
    @Override
    public boolean save(OVChipkaart ovChipkaart) {
        try {
            String insertQuery = "INSERT INTO ov-chipkaart (kaart_nummer, geldig_tot, klasse, saldo, reiziger_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
            preparedStatement.setInt(1, ovChipkaart.getKaart_nummer());
            preparedStatement.setDate(2, Date.valueOf(ovChipkaart.getGeldig_tot()));
            preparedStatement.setInt(3, ovChipkaart.getKlasse());
            preparedStatement.setDouble(4, ovChipkaart.getSaldo());
            preparedStatement.setInt(5, ovChipkaart.getReiziger().getId());

            preparedStatement.executeUpdate();
            preparedStatement.close();
            if(productDAOsql!= null){
                for(Product product : ovChipkaart.getProducten()){
                    productDAOsql.save(product);
                }

            }
            return true;
        } catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(OVChipkaart ovChipkaart) {
        try {
            String updateQuery = "UPDATE ov-chipkaart SET geldig_tot = ?, klasse = ?, saldo = ?, reiziger_id = ? WHERE kaart_nummer = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);
            preparedStatement.setDate(1, Date.valueOf(ovChipkaart.getGeldig_tot()));
            preparedStatement.setInt(2, ovChipkaart.getKlasse());
            preparedStatement.setDouble(3, ovChipkaart.getSaldo());
            preparedStatement.setInt(4, ovChipkaart.getReiziger().getId());
            preparedStatement.setInt(5, ovChipkaart.getKaart_nummer());

            preparedStatement.executeUpdate();
            preparedStatement.close();
            for(Product product: ovChipkaart.getProducten()){
                productDAOsql.update(product);
            }
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(OVChipkaart ovChipkaart) {
        try {
            for(Product product:ovChipkaart.getProducten()){
                productDAOsql.unlinkProductOV(ovChipkaart.getKaart_nummer(), product);
            }
            String deleteQuery = "DELETE FROM ov-chipkaart WHERE kaart_nummer = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(deleteQuery);
            preparedStatement.setInt(1, ovChipkaart.getKaart_nummer());

            preparedStatement.execute();
            preparedStatement.close();

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());;
        }
        return false;
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) {
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT 
                        kaart_nummer, 
                        geldig_tot, 
                        klasse, 
                        saldo
                    FROM 
                        ov_chipkaart 
                    WHERE 
                        reiziger_id = ?
                    """);
            ps.setInt(1, reiziger.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                OVChipkaart ovChipkaart = new OVChipkaart(rs.getInt("kaart_nummer"), rs.getDate("geldig_tot").toLocalDate(), rs.getInt("klasse"), rs.getDouble("saldo"), reiziger);
                ovChipkaarten.add(ovChipkaart);
                List<Product> producten = productDAOsql.findByOVChipkaart(ovChipkaart);
                if(producten != null){
                    ovChipkaart.setProducten(producten);
                }
            }
            rs.close();
            ps.close();
        }catch (SQLException e){
            System.err.println(e.getMessage());
            ovChipkaarten = null;
        }
        return ovChipkaarten;
    }

    @Override
    public List<OVChipkaart> findAll(){

        List<OVChipkaart> ovChipkaarten = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("""
                SELECT 
                    kaart_nummer, 
                    geldig_tot, 
                    klasse, 
                    saldo,
                    reiziger_id
                FROM 
                    ov_chipkaart 
             
                """);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                OVChipkaart ovChipkaart = new OVChipkaart(rs.getInt("kaart_nummer"), rs.getDate("geldig_tot").toLocalDate(), rs.getInt("klasse"), rs.getDouble("saldo"), rdao.findById(rs.getInt("reiziger_id")));

                List<Product> producten = productDAOsql.findByOVChipkaart(ovChipkaart);
                if(producten != null){
                    ovChipkaart.setProducten(producten);
                }
                ovChipkaarten.add(ovChipkaart);

            }
            rs.close();
            ps.close();
        }catch (SQLException e){
            System.err.println(e.getMessage());
            ovChipkaarten = null;
        }
        return ovChipkaarten;

    }
}

package application;

import data.AdresDAO;
import domain.Adres;
import domain.Reiziger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdresDAOsql implements AdresDAO {
    private final Connection conn;

    public AdresDAOsql(Connection conn) {
        this.conn = conn;
    }
    @Override
    public boolean save(Adres adres) {
        String sql = "insert into adres (adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id) values (?, ?, ?, ?, ?, ?)";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, adres.getAdres_id());
            ps.setString(2, adres.getPostcode());
            ps.setString(3, adres.getHuisnummer());
            ps.setString(4, adres.getStraat());
            ps.setString(5, adres.getWoonplaats());
            ps.setInt(6, adres.getReiziger().getId());
            ps.execute();
            ps.close();
            return true;
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(Adres adres) {
        String sql = "update adres set postcode = ?, huisnummer = ?, straat = ?, woonplaats = ?, reiziger_id = ? where adres_id = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, adres.getPostcode());
            ps.setString(2, adres.getHuisnummer());
            ps.setString(3, adres.getStraat());
            ps.setString(4, adres.getWoonplaats());
            ps.setInt(5, adres.getReiziger_id());
            ps.setInt(6, adres.getAdres_id());

            ps.execute();
            ps.close();
            return true;
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(Adres adres) {
        try{
            String sql = "delete from adres where adres_id = ?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, adres.getAdres_id());

            ps.execute();
            ps.close();
            return true;
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return false;
    }

    @Override
    public Adres findById(int id) throws SQLException {
        try{
            Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery(String.format("select * from adres where adres_id=%d", id));
            if(res.next()){
                int adresId = res.getInt("adres_id");
                String postcode = res.getString("postcode");
                String huisnummer = res.getString("huisnummer");
                String straat = res.getString("straat");
                String woonplaats = res.getString("woonplaats");
                int reisId = res.getInt("reiziger_id");

                res.close();
                statement.close();

                return new Adres(adresId, postcode, huisnummer, straat, woonplaats, reisId);
            }
        }catch (SQLException ex) {
            System.out.println("Adres niet gevonden");
        }
        return null;
    }

    @Override
    public Adres findByReiziger(Reiziger r) throws SQLException {
        try{
            Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery(String.format("select * from adres where reiziger_id=%d", r.getId()));
            if(res.next()){
                int adresId = res.getInt("adres_id");
                String postcode = res.getString("postcode");
                String huisnummer = res.getString("huisnummer");
                String straat = res.getString("straat");
                String woonplaats = res.getString("woonplaats");
                int reisId = res.getInt("reiziger_id");

                res.close();
                statement.close();

                return new Adres(adresId, postcode, huisnummer, straat, woonplaats, reisId);
            }
        }catch (SQLException ex) {
            System.out.println("Adres niet gevonden");
        }
        return null;
    }

    @Override
    public List<Adres> findAll() throws SQLException {
        List<Adres> list = new ArrayList<>();

        try{

            Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery("select * from adres");

            while(res.next()){
                int Id = res.getInt("adres_id");
                String postcode = res.getString("postcode");
                String huisnr = res.getString("huisnummer");
                String straat = res.getString("straat");
                String woonplaats = res.getString("woonplaats");
                int reiz_id = res.getInt("reiziger_id");
                list.add(new Adres(Id, postcode, huisnr, straat, woonplaats, reiz_id));
            }

            res.close();
            statement.close();

        }catch (SQLException ex){
            System.out.println("Geen adres gevonden");
        }
        return list;
    }
}

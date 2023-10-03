package application;

import data.AdresDAO;
import data.ReizigerDAO;
import domain.Adres;
import domain.Reiziger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdresDAOsql implements AdresDAO {
    private final Connection conn;
    private ReizigerDAO rdao;
    public AdresDAOsql(Connection conn, ReizigerDAO rdao) {
        this.conn = conn;
        this.rdao = rdao;
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
            ps.setInt(5, adres.getReiziger().getId());
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
    public Adres findByReiziger(Reiziger r) throws SQLException {
        try{
            Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery(String.format("select adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id from adres where reiziger_id=%d", r.getId()));
            if(res.next()){
                int adresId = res.getInt("adres_id");
                String postcode = res.getString("postcode");
                String huisnummer = res.getString("huisnummer");
                String straat = res.getString("straat");
                String woonplaats = res.getString("woonplaats");
                Adres a = new Adres(adresId, postcode, huisnummer, straat, woonplaats, r);
                res.close();
                statement.close();

                return a;
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
            ResultSet res = statement.executeQuery("select adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id from adres");

            while(res.next()){
                int Id = res.getInt("adres_id");
                String postcode = res.getString("postcode");
                String huisnr = res.getString("huisnummer");
                String straat = res.getString("straat");
                String woonplaats = res.getString("woonplaats");
                int reiz_id = res.getInt("reiziger_id");
                Adres a = new Adres(Id, postcode, huisnr, straat, woonplaats, rdao.findById(reiz_id));
                list.add(a);
            }

            res.close();
            statement.close();

        }catch (SQLException ex){
            System.out.println("Geen adres gevonden");
        }
        return list;
    }
}

package application;

import data.AdresDAO;
import data.ReizigerDAO;
import domain.OVChipkaart;
import domain.Reiziger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOsql implements ReizigerDAO {

    private final Connection conn;
    private AdresDAO adao;
    private OVChipkaartDAOsql odao;

    public ReizigerDAOsql(Connection conn) {
        this.conn = conn;
        this.adao = new AdresDAOsql(conn, this);
        this.odao = new OVChipkaartDAOsql(conn, this);
    }

    @Override
    public boolean save(Reiziger reiziger) {
        String sql = "insert into reiziger (reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum) values (?, ?, ?, ?, ?)";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, reiziger.getId());
            ps.setString(2, reiziger.getVoorletter());
            ps.setString(3, reiziger.getTussenvoegsel());
            ps.setString(4, reiziger.getAchternaam());
            ps.setDate(5, Date.valueOf(reiziger.getGbDatum()));
            ps.execute();
            ps.close();
            if(reiziger.getAdres()!=null){
                if(this.adao != null){
                    this.adao.save(reiziger.getAdres());
                }
            }
            if (reiziger.getOvChipkaarten() != null && !reiziger.getOvChipkaarten().isEmpty()) {
                for (OVChipkaart ovChipkaart : reiziger.getOvChipkaarten()) {
                    ovChipkaart.setReiziger(reiziger);
                    this.odao.save(ovChipkaart);
                }
            }
            return true;
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(Reiziger reiziger) {
        String sql = "update reiziger set voorletters = ?, tussenvoegsel = ?, achternaam = ?, geboortedatum = ? where reiziger_id = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, reiziger.getVoorletter());
            ps.setString(2, reiziger.getTussenvoegsel());
            ps.setString(3, reiziger.getAchternaam());
            ps.setDate(4, Date.valueOf(reiziger.getGbDatum()));
            ps.setInt(5, reiziger.getId());

            ps.execute();
            ps.close();
            if(reiziger.getAdres()!=null){
                if(this.adao != null){
                    this.adao.update(reiziger.getAdres());
                }
            }
            if (reiziger.getOvChipkaarten() != null && !reiziger.getOvChipkaarten().isEmpty()) {
                for (OVChipkaart ovChipkaart : reiziger.getOvChipkaarten()) {
                    ovChipkaart.setReiziger(reiziger);
                    this.odao.update(ovChipkaart);
                }
            }
            return true;
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(Reiziger reiziger) {
        try{
            if(reiziger.getAdres()!=null){
                if(this.adao != null){
                    this.adao.delete(reiziger.getAdres());
                }
            }
            if (reiziger.getOvChipkaarten() != null && !reiziger.getOvChipkaarten().isEmpty()) {
                for (OVChipkaart ovChipkaart : reiziger.getOvChipkaarten()) {
                    ovChipkaart.setReiziger(reiziger);
                    this.odao.delete(ovChipkaart);
                }
            }
            String sql = "delete from reiziger where reiziger_id = ?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, reiziger.getId());

            ps.execute();
            ps.close();

            return true;
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return false;
    }

    @Override
    public Reiziger findById(int id) throws SQLException {
        try{
            Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery(String.format("SELECT voorletters, tussenvoegsel, achternaam, geboortedatum FROM reiziger WHERE reiziger_id = %d", id));
            if(res.next()){
                String fL = res.getString("voorletters");
                String mN = res.getString("tussenvoegsel");
                String lN = res.getString("achternaam");
                LocalDate bD = res.getDate("geboortedatum").toLocalDate();

                res.close();
                statement.close();
                Reiziger r = new Reiziger(id, fL, mN, lN, bD);
                r.setAdres(adao.findByReiziger(r));
                r.setOvChipkaarten(odao.findByReiziger(r));
                return r;
            }
        }catch (SQLException ex) {
            System.out.println("Reiziger niet gevonden");
        }
        return null;
    }

    @Override
    public List<Reiziger> findByGbdatum(String datum) throws SQLException {
        List<Reiziger> reizigers = new ArrayList<>();
        try{
            Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery(String.format("select * from reiziger where geboortedatum='%s'", datum));

            while(res.next()){

                int Id = res.getInt("reiziger_id");
                String fL = res.getString("voorletters");
                String mN = res.getString("tussenvoegsel");
                String lN = res.getString("achternaam");
                LocalDate bD = res.getDate("geboortedatum").toLocalDate();
                Reiziger r = new Reiziger(Id, fL, mN, lN, bD);
                r.setAdres(adao.findByReiziger(r));
                r.setOvChipkaarten(odao.findByReiziger(r));
                reizigers.add(r);
            }

            res.close();
            statement.close();

        }catch (SQLException ex){
            System.out.println("Kan geen reizigers vinden bij deze geboortedatum");
        }
        return reizigers;
    }

    @Override
    public List<Reiziger> findAll() throws SQLException {
        List<Reiziger> list = new ArrayList<>();

        try{

            Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery("select reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum from reiziger");

            while(res.next()){
                int Id = res.getInt("reiziger_id");
                String fL = res.getString("voorletters");
                String mN = res.getString("tussenvoegsel");
                String lN = res.getString("achternaam");
                LocalDate bD = res.getDate("geboortedatum").toLocalDate();
                Reiziger r = new Reiziger(Id, fL, mN, lN, bD);
                r.setAdres(adao.findByReiziger(r));
                r.setOvChipkaarten(odao.findByReiziger(r));
                list.add(r);
            }

            res.close();
            statement.close();

        }catch (SQLException ex){
            System.out.println("Geen reizigers gevonden");
        }
        return list;
    }
}

package application;

import data.OVChipkaartDAO;
import data.ReizigerDAO;
import domain.Adres;
import domain.OVChipkaart;
import domain.Reiziger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOsql implements OVChipkaartDAO {
    private final Connection conn;
    private ReizigerDAO rdao;
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

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(OVChipkaart ovChipkaart) {
        try {
            String deleteQuery = "DELETE FROM ov-chipkaart WHERE kaart_nummer = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(deleteQuery);
            preparedStatement.setInt(1, ovChipkaart.getKaart_nummer());

            preparedStatement.executeUpdate();
            preparedStatement.close();

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());;
        }
        return false;
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger r) throws SQLException {
        List<OVChipkaart> ovChipkaartList = new ArrayList<>();

        String selectQuery = "SELECT kaart_nummer, geldig_tot, klasse, saldo FROM ov-chipkaart WHERE reiziger_id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(selectQuery);
        preparedStatement.setInt(1, r.getId());

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            int kaartNummer = resultSet.getInt("kaart_nummer");
            LocalDate geldigTot = resultSet.getDate("geldig_tot").toLocalDate();
            int klasse = resultSet.getInt("klasse");
            double saldo = resultSet.getDouble("saldo");

            OVChipkaart ovChipkaart = new OVChipkaart(kaartNummer, geldigTot, klasse, saldo, r);
            ovChipkaartList.add(ovChipkaart);
        }

        resultSet.close();
        preparedStatement.close();

        return ovChipkaartList;
    }

    @Override
    public List<OVChipkaart> findAll() throws SQLException {
        List<OVChipkaart> ovChipkaartList = new ArrayList<>();

        String selectQuery = "SELECT kaart_nummer, geldig_tot, klasse, saldo, reiziger_id FROM ov-chipkaart";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(selectQuery);

        while (resultSet.next()) {
            int kaartNummer = resultSet.getInt("kaart_nummer");
            LocalDate geldigTot = resultSet.getDate("geldig_tot").toLocalDate();
            int klasse = resultSet.getInt("klasse");
            double saldo = resultSet.getDouble("saldo");
            int reizigerId = resultSet.getInt("reiziger_id");


            Reiziger reiziger = rdao.findById(reizigerId);

            OVChipkaart ovChipkaart = new OVChipkaart(kaartNummer, geldigTot, klasse, saldo, reiziger);
            ovChipkaartList.add(ovChipkaart);
        }

        resultSet.close();
        statement.close();

        return ovChipkaartList;
    }
}

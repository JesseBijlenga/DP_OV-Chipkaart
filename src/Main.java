import java.sql.*;
public class Main {
    public static void main(String[] args) throws SQLException {

        String dbUrl = "jdbc:postgresql://localhost:5432/ovchip";
        String user = "postgres";
        String pass = "123";

        Connection cn = DriverManager.getConnection(dbUrl, user, pass);
        Statement st = cn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM reiziger");
        while (rs.next()) {
            String tussenvoegsel;
if (rs.getString(3) == null){
     tussenvoegsel = "";
} else{
    tussenvoegsel = " "+ rs.getString(3);
}

            System.out.println("#"+ rs.getString(1)+ ": "+rs.getString(2) + "." + tussenvoegsel + " " + rs.getString(4) + " (" + rs.getString(5)+")");
        }
        rs.close();
        st.close();
    }
}
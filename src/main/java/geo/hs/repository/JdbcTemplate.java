package geo.hs.repository;

import java.sql.*;
import java.util.ArrayList;

public class JdbcTemplate {
    private final String connectUrl;
    private final String user;
    private final String password;
    private final String tableName;

    public JdbcTemplate(String tableName) {
        connectUrl = "jdbc:postgresql://localhost:5432/geor";
        user = "postgres";
        password = "1";
        this.tableName = tableName;
        setClass();
    }

    public void setClass() {

    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection conn = DriverManager.getConnection(connectUrl, user, password);
        conn.setAutoCommit(false);
        return conn;
    }

    /**
     * db에 테이블이 있는지 확인
     * @return false : table 있음
     *         true  : table 없음
     */
    public boolean tableNotExist(Connection conn) throws SQLException {
        DatabaseMetaData dbm = conn.getMetaData();
        ResultSet tables = dbm.getTables(null, null, tableName, null);
        if(!tables.next()) {
            System.out.println(tableName + "이 이 없습니다. table을 생성합니다...");
            return true;
        }
        return false;
    }

    public ArrayList<String> getColumns(Connection conn) throws SQLException {
        ArrayList<String> columns = new ArrayList<>();
        String Query = "select * from public." + tableName;
        try(PreparedStatement pStmt = conn.prepareStatement(Query)) {
            ResultSetMetaData meta = pStmt.getMetaData();
            for (int i=1; i <= meta.getColumnCount(); i++)
            {
                // System.out.println("Column name: " + meta.getColumnName(i) + ", data type: " + meta.getColumnTypeName(i));
                columns.add(meta.getColumnName(i));
            }
        }
        return columns;
    }

    public void createTable(Connection conn) {
        String createQuery = getCreateQuery();
        try (Statement st = conn.createStatement()) {
            st.execute(createQuery);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS public.shp\n" +
                "(\n" +
                "    the_geom geometry(MultiPolygon,5179),\n" +
                "    \"RBID\" varchar(15), \n" +
                "    \"HJCD\" varchar(5), \n" +
                "    \"UOPS\" integer, \n" +
                "    \"TPSL\" integer,\n" +
                "    \"MMSL\" integer,\n" +
                "    \"FCLT\" integer,\n" +
                "    \"RDNU\" varchar(30), \n" +
                "    \"NAME\" varchar(100), \n" +
                "    \"RDDV\" varchar(6), \n" +
                "    \"STPT\" varchar(100), \n" +
                "    \"EDPT\" varchar(100), \n" +
                "    \"PVQT\" varchar(6), \n" +
                "    \"DVYN\" varchar(6), \n" +
                "    \"RDLN\" integer,\n" +
                "    \"RVWD\" double precision,\n" +
                "    \"ONSD\" varchar(6), \n" +
                "    \"RDNM\" varchar(30), \n" +
                "    \"AREA\" double precision,\n" +
                "    \"REST\" varchar(50), \n" +
                "    \"Shape_Leng\" double precision,\n" +
                "    \"Shape_Area\" double precision,\n" +
                "    hillshade integer default 0\n" +
                ")";
    }
}

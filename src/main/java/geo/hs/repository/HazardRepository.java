package geo.hs.repository;

import geo.hs.model.hazard.Hazard;

import java.sql.*;
import java.util.ArrayList;

public class HazardRepository {

    public void saveBridge(ArrayList<Hazard> hazards) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/geor";
        String user = "postgres";
        String password = ""; //password 입력
        try
        {
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password);

            if(connect != null) {
                System.out.println("Connection successful!!");
            }
            else {
                throw new SQLException("no connection...");
            }

            connect.setAutoCommit(false);

            // sql문
            String sql = "INSERT INTO bridge VALUES(?, ?)";
            PreparedStatement ps = connect.prepareStatement(sql);

            for(Hazard hazard : hazards){
                ps.setDouble(1, hazard.getLatitude());
                ps.setDouble(2, hazard.getLongitude());
                ps.addBatch();
                ps.clearParameters();
            }

            ps.executeBatch();
            ps.clearParameters(); //Batch 초기화
            connect.commit();

        } catch (SQLException ex) {
            throw ex;
        }
    }

    public ArrayList<Hazard> getBridge() throws SQLException {
        ArrayList<Hazard> hazards = new ArrayList<>();

        String url = "jdbc:postgresql://localhost:5432/geor";
        String user = "postgres";
        String password = ""; //password 입력
        try
        {
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password);

            if(connect != null) {
                System.out.println("Connection successful!!");
            }
            else {
                throw new SQLException("no connection...");
            }


            Statement stmt = connect.createStatement();
            // sql문
            String sql = "SELECT latitude, longitude FROM bridge";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                Double latitude = rs.getDouble("latitude");
                Double longitude = rs.getDouble("longitude");

                if(latitude == 0.0 || longitude == 0.0) continue;

                Hazard hazard = new Hazard(latitude, longitude);
                hazards.add(hazard);
            }


        } catch (SQLException ex) {
            throw ex;
        }
        return hazards;
    }

    public void saveTunnel(ArrayList<Hazard> hazards) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/geor";
        String user = "postgres";
        String password = ""; //password 입력
        try
        {
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password);

            if(connect != null) {
                System.out.println("Connection successful!!");
            }
            else {
                throw new SQLException("no connection...");
            }

            connect.setAutoCommit(false);

            // sql문
            String sql = "INSERT INTO tunnel VALUES(?, ?)";
            PreparedStatement ps = connect.prepareStatement(sql);

            for(Hazard hazard : hazards){
                ps.setDouble(1, hazard.getLatitude());
                ps.setDouble(2, hazard.getLongitude());
                ps.addBatch();
                ps.clearParameters();
            }

            ps.executeBatch();
            ps.clearParameters(); //Batch 초기화
            connect.commit();

        } catch (SQLException ex) {
            throw ex;
        }
    }

    public ArrayList<Hazard> getTunnel() throws SQLException {
        ArrayList<Hazard> hazards = new ArrayList<>();

        String url = "jdbc:postgresql://localhost:5432/geor";
        String user = "postgres";
        String password = ""; //password 입력
        try
        {
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password);

            if(connect != null) {
                System.out.println("Connection successful!!");
            }
            else {
                throw new SQLException("no connection...");
            }


            Statement stmt = connect.createStatement();
            // sql문
            String sql = "SELECT latitude, longitude FROM tunnel";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                Double latitude = rs.getDouble("latitude");
                Double longitude = rs.getDouble("longitude");

                if(latitude == 0.0 || longitude == 0.0) continue;

                Hazard hazard = new Hazard(latitude, longitude);
                hazards.add(hazard);
            }


        } catch (SQLException ex) {
            throw ex;
        }
        return hazards;
    }

    /*
    이전 코드 -> 해저드를 hillshade로 처리했던 작년 코드
    private final ConvertSRID convertSRID;
    private final GeometryFactory geometryFactory;
    public HazardRepository() {
        convertSRID = new ConvertSRID();
        geometryFactory = JTSFactoryFinder.getGeometryFactory();
    }
    public void applyHazard(Connection conn, ArrayList<Hazard> lake, ArrayList<Hazard> turnel) throws SQLException {
        String query = getUpdateQuery();
        try (PreparedStatement pStmt = conn.prepareStatement(query)) {
            setObject(pStmt, lake);
            setObject(pStmt, turnel);
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }
    public void setObject(PreparedStatement pStmt, ArrayList<Hazard> hazards) throws SQLException {
        for (Hazard hazard : hazards) {
            try {
                Coordinate coords = convertSRID.convertCoordinate(hazard.getLongitude(), hazard.getLatitude());
                Point point = geometryFactory.createPoint(coords);
                pStmt.setInt(1, -9999);
                pStmt.setObject(2, point.toString());
                pStmt.addBatch();
            } catch (TransformException e) {
                e.printStackTrace();
            }
        }
        System.out.println("update records : " + pStmt.executeBatch().length);
        pStmt.clearBatch();
    }
    private String getUpdateQuery() {
        StringBuilder query = new StringBuilder("update public.");
        query.append(getProperty("shp.table"));
        query.append(" AS SHP");
        query.append(" set hillshade = ?");
        query.append(" where ST_Within(ST_GeometryFromText(?, 5179), SHP.the_geom)");
        return query.toString();
    }
     */
}
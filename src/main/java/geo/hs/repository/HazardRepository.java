package geo.hs.repository;

import geo.hs.model.hazard.Hazard;

import java.sql.*;
import java.util.ArrayList;

public class HazardRepository {
    private final String url = "jdbc:postgresql://localhost:5431/geor";
    private final String user = "geor";
    String password = "geor"; // password 입력

    public void saveBridge(ArrayList<Hazard> hazards) throws SQLException {
        try {
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password);

            if (connect != null) {
                System.out.println("Connection successful!!");
            } else {
                throw new SQLException("no connection...");
            }

            connect.setAutoCommit(false);

            // sql문
            String sql = "INSERT INTO bridge VALUES(?, ?)";
            PreparedStatement ps = connect.prepareStatement(sql);

            for (Hazard hazard : hazards) {
                ps.setDouble(1, hazard.getLatitude());
                ps.setDouble(2, hazard.getLongitude());
                ps.addBatch();
                ps.clearParameters();
            }

            ps.executeBatch();
            ps.clearParameters(); // Batch 초기화
            connect.commit();

        } catch (SQLException ex) {
            throw ex;
        }
    }

    public ArrayList<Hazard> getBridge() throws SQLException {
        ArrayList<Hazard> hazards = new ArrayList<>();

        try {
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password);

            if (connect != null) {
                System.out.println("Connection successful!!");
            } else {
                throw new SQLException("no connection...");
            }

            Statement stmt = connect.createStatement();
            // sql문
            String sql = "SELECT latitude, longitude FROM bridge";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Double latitude = rs.getDouble("latitude");
                Double longitude = rs.getDouble("longitude");

                if (latitude == 0.0 || longitude == 0.0)
                    continue;

                Hazard hazard = new Hazard(latitude, longitude);
                hazards.add(hazard);
            }

        } catch (SQLException ex) {
            throw ex;
        }
        return hazards;
    }

    public void saveTunnel(ArrayList<Hazard> hazards) throws SQLException {
        try {
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password);

            if (connect != null) {
                System.out.println("Connection successful!!");
            } else {
                throw new SQLException("no connection...");
            }

            connect.setAutoCommit(false);

            // sql문
            String sql = "INSERT INTO tunnel VALUES(?, ?)";
            PreparedStatement ps = connect.prepareStatement(sql);

            for (Hazard hazard : hazards) {
                ps.setDouble(1, hazard.getLatitude());
                ps.setDouble(2, hazard.getLongitude());
                ps.addBatch();
                ps.clearParameters();
            }

            ps.executeBatch();
            ps.clearParameters(); // Batch 초기화
            connect.commit();

        } catch (SQLException ex) {
            throw ex;
        }
    }

    public ArrayList<Hazard> getTunnel() throws SQLException {
        ArrayList<Hazard> hazards = new ArrayList<>();

        try {
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password);

            if (connect != null) {
                System.out.println("Connection successful!!");
            } else {
                throw new SQLException("no connection...");
            }

            Statement stmt = connect.createStatement();
            // sql문
            String sql = "SELECT latitude, longitude FROM tunnel";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Double latitude = rs.getDouble("latitude");
                Double longitude = rs.getDouble("longitude");

                if (latitude == 0.0 || longitude == 0.0)
                    continue;

                Hazard hazard = new Hazard(latitude, longitude);
                hazards.add(hazard);
            }

        } catch (SQLException ex) {
            throw ex;
        }
        return hazards;
    }

}
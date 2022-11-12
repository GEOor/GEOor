package geo.hs.repository;

import geo.hs.model.hazard.Hazard;
import org.geotools.geometry.jts.WKBReader;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class HazardRepository {
    private final String url = "jdbc:postgresql://localhost:5431/geor";
    private final String user = "geor";
    private final String password = "geor"; // password 입력

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
        WKBReader wkbReader = new WKBReader();

        try {
            Connection connect = DriverManager.getConnection(url, user, password);

            Statement stmt = connect.createStatement();
            // sql문
            String sql = "SELECT the_geom FROM bridge";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                PGobject pGobject = (PGobject) rs.getObject("the_geom");
                byte[] geom = WKBReader.hexToBytes(Objects.requireNonNull(pGobject.getValue()));
                Point point = (Point) wkbReader.read(geom);

                double latitude = point.getX();
                double longitude = point.getY();

                if (latitude == 0.0 || longitude == 0.0)
                    continue;

                hazards.add(new Hazard(latitude, longitude));
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
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
        WKBReader wkbReader = new WKBReader();

        try {
            Connection connect = DriverManager.getConnection(url, user, password);

            Statement stmt = connect.createStatement();

            String sql = "SELECT the_geom FROM tunnel";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                PGobject pGobject = (PGobject) rs.getObject("the_geom");
                byte[] geom = WKBReader.hexToBytes(Objects.requireNonNull(pGobject.getValue()));
                Point point = (Point) wkbReader.read(geom);

                double latitude = point.getX();
                double longitude = point.getY();

                if (latitude == 0.0 || longitude == 0.0)
                    continue;

                hazards.add(new Hazard(latitude, longitude));
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return hazards;
    }

    public ArrayList<Hazard> getFrozen() throws SQLException {
        ArrayList<Hazard> hazards = new ArrayList<>();
        WKBReader wkbReader = new WKBReader();

        try {
            Connection connect = DriverManager.getConnection(url, user, password);

            Statement stmt = connect.createStatement();

            String sql = "SELECT the_geom FROM frozen";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                PGobject pGobject = (PGobject) rs.getObject("the_geom");
                byte[] geom = WKBReader.hexToBytes(Objects.requireNonNull(pGobject.getValue()));
                Point point = (Point) wkbReader.read(geom);

                double latitude = point.getX();
                double longitude = point.getY();

                if (latitude == 0.0 || longitude == 0.0)
                    continue;

                hazards.add(new Hazard(latitude, longitude));
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return hazards;
    }
}
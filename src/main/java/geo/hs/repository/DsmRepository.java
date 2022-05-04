package geo.hs.repository;

import geo.hs.algorithm.coordinate.ConvertSRID;
import geo.hs.model.hillshade.Hillshade;
import geo.hs.model.shp.RoadStatus;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.opengis.referencing.operation.TransformException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static geo.hs.config.ApplicationProperties.getProperty;


public class DsmRepository {

    private final ConvertSRID convertSRID;
    private final GeometryFactory geometryFactory;
    private HashMap<String, RoadStatus> roadStatusHashMap = new HashMap();

    public DsmRepository() {
        convertSRID = new ConvertSRID();
        geometryFactory = JTSFactoryFinder.getGeometryFactory();
    }

    public void findOverlapPolygon(Connection conn, ArrayList<Hillshade> hillshadeArr) throws SQLException {
        String findQuery = getOverLapPolygonQuery();
        try (PreparedStatement pStmt = conn.prepareStatement(findQuery)) {
            int hillshadeArrSize = hillshadeArr.size();
            for (int i = hillshadeArrSize - 1; i >= 0 ; i--) {
                if(hillshadeArr.get(i).getGrid().size() < 8)
                    continue;
                Polygon polygon = MakePolygon(hillshadeArr.get(i).getGrid());
                pStmt.setObject(1, polygon.toString());
                getResult(pStmt, hillshadeArr.get(i).getHillshade());
//                System.out.println(i + " / " + hillshadeArrSize);
            }
//            print();
        }
    }

    public String getOverLapPolygonQuery() {
        StringBuilder query = new StringBuilder("select \"RBID\" from public.");
        query.append(getProperty("shp.table"));
        query.append(" AS SHP where ST_Overlaps(ST_GeometryFromText(?, 5179), SHP.the_geom)");
        System.out.println(query);
        return query.toString();
    }

    private Polygon MakePolygon(ArrayList<Double> grid) {
        Coordinate[] coords = new Coordinate[5];
        try {
            coords[0] = convertSRID.convertCoordinate(grid.get(0), grid.get(1));
            coords[1] = convertSRID.convertCoordinate(grid.get(2), grid.get(3));
            coords[2] = convertSRID.convertCoordinate(grid.get(4), grid.get(5));
            coords[3] = convertSRID.convertCoordinate(grid.get(6), grid.get(7));
            coords[4] = coords[0];
        } catch (TransformException e) {
            e.printStackTrace();
        }
        LinearRing ring = geometryFactory.createLinearRing(coords);
        Polygon polygon = geometryFactory.createPolygon(ring, null);
        return polygon;
    }

    /**
     * dem을 polygon으로 만든 후, shp table과 overlap 확인해 RBID(도로 ID) 가져온 후
     * 해당 RBID와 매칭되는 RoadStatus 객체를 가져온다.
     * dem의 hillshade 값을 RoadStatus 객체의 hillshade 멤버변수에 더해주고
     * count 멤버변수를 1 더한다.
     */
    private void getResult(PreparedStatement pStmt, double hillshade) {
        try (ResultSet rs = pStmt.executeQuery()) {
            while (rs.next()) {
                String rbid = rs.getString("rbid");
                if (roadStatusHashMap.containsKey(rbid))
                    roadStatusHashMap.get(rbid).addHillShade(hillshade);
                else {
                    roadStatusHashMap.put(rbid, new RoadStatus(rbid, hillshade));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // test용
    private void print() {
        System.out.println(roadStatusHashMap.size());
        for (HashMap.Entry<String, RoadStatus> entry : roadStatusHashMap.entrySet()) {
            RoadStatus roadStatus = entry.getValue();
            System.out.println("RBID = " + roadStatus.getRbid()
                    + " hillshade = " + roadStatus.getHillShade()
                    + " count = " + roadStatus.getCount());
        }
    }

    public void updateHillShade(Connection conn) throws SQLException {
        String query = getUpdateQuery();
        System.out.println(query);
        try (PreparedStatement pStmt = conn.prepareStatement(query)) {
            for (HashMap.Entry<String, RoadStatus> entry : roadStatusHashMap.entrySet()) {
                RoadStatus roadStatus = entry.getValue();
                pStmt.setInt(1,(int)(roadStatus.getHillShade() / roadStatus.getCount()));
                pStmt.setString(2, roadStatus.getRbid());
                pStmt.addBatch();
            }
            pStmt.executeBatch();
            pStmt.clearBatch();
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }

    private String getUpdateQuery() {
        StringBuilder query = new StringBuilder("update public.");
        query.append(getProperty("shp.table"));
        query.append(" as SHP set hillshade = ?");
        query.append("where SHP.\"RBID\" like ?");
        return query.toString();
    }
}

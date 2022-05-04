package geo.hs.repository;

import geo.hs.model.shp.Shp;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKBWriter;
import org.opengis.feature.simple.SimpleFeature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static geo.hs.config.ApplicationProperties.getProperty;


public class ShpSaveRepository {
    private final WKBWriter writer = new WKBWriter();

    public void save(Connection conn, ArrayList<Shp> shps, ArrayList<String> columns) throws SQLException {
        String insertQuery = getInsertQuery(columns);
        int featureCount = 0;
        try (PreparedStatement pStmt = conn.prepareStatement(insertQuery)) {
            for (Shp shp : shps) {
                System.out.print(shp.getFile().getName() + " save... ");
                featureCount = saveShp(pStmt, shp, columns);
                System.out.println(featureCount);
            }
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }

    private String getInsertQuery(ArrayList<String> columns) {
        StringBuilder query = new StringBuilder("insert into " + getProperty("shp.table") + " values (");
        // hillshade 값 하나는 제외시킴
        for (int i = 0; i < columns.size() - 2; i++) {
            query.append("?, ");
        }
        query.append("?);");
        //System.out.println(query);
        return query.toString();
    }

    private int saveShp(PreparedStatement pStmt, Shp shp, ArrayList<String> columns) throws SQLException {
        int featureCount = 0;
        FeatureIterator<SimpleFeature> features = shp.getFeature();
        while (features.hasNext()) {
            SimpleFeature feature = features.next();
            setShpObject(pStmt, feature, columns);
            featureCount++;
        }
        features.close();
        pStmt.executeBatch();
        pStmt.clearBatch();
        return featureCount;
    }

    private void setShpObject(PreparedStatement pStmt, SimpleFeature feature, ArrayList<String> columns) throws SQLException {
        pStmt.setObject(1, writer.write((Geometry) feature.getDefaultGeometryProperty().getValue()));
        // 하나 빼는 이유는 hillshade 기본값 0으로 둘려고
        for (int i = 1; i < columns.size() - 1; i++) {
            pStmt.setObject(i+1, feature.getAttribute(columns.get(i)));
        }
        pStmt.addBatch();
    }
}

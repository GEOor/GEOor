package geo.hs.geoUtil;


import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

public class SRID {

    private CoordinateReferenceSystem sourceCrs;    // EPSG 4326
    private CoordinateReferenceSystem targetCrs;    // EPSG 5181
    private MathTransform engine;

    public SRID() {
        try {
            // reference : http://www.gisdeveloper.co.kr/?p=8942
            sourceCrs = CRS.decode("EPSG:4326");
            targetCrs = CRS.decode("EPSG:5181");
            // EPSG 4326을 시작 좌표, EPSG 5181을 목표 좌표로 한다.
            engine = CRS.findMathTransform(sourceCrs, targetCrs, true);
        } catch (FactoryException e) {
            e.printStackTrace();
        }
        // X 좌표가 먼저 오도록 설정. 즉, longitude 먼저 나온다.
        System.setProperty("org.geotools.referencing.forceXY", "true");
    }
    // EPSG 4326 -> EPSG 5181 변환
    public Coordinate convertPoint(double longitude, double latitude) {
        DirectPosition2D source = new DirectPosition2D(sourceCrs, longitude, latitude);
        DirectPosition target = new DirectPosition2D(targetCrs);

        try {
            engine.transform(source, target);
        } catch (TransformException e) {
            e.printStackTrace();
        }
        return new Coordinate(target.getCoordinate()[0], target.getCoordinate()[1]);
    }
    // EPSG 5181 -> EPSG 4326 변환
    public Coordinate revertPoint(double longitude, double latitude) {
        DirectPosition2D source = new DirectPosition2D(targetCrs, longitude, latitude);
        DirectPosition target = new DirectPosition2D(sourceCrs);

        try {
            engine.transform(source, target);
        } catch (TransformException e) {
            e.printStackTrace();
        }
        return new Coordinate(target.getCoordinate()[0], target.getCoordinate()[1]);
    }

}

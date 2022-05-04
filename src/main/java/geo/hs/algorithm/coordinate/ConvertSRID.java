package geo.hs.algorithm.coordinate;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

public class ConvertSRID {
    private CoordinateReferenceSystem sourceCrs;
    private CoordinateReferenceSystem targetCrs;
    private MathTransform engine;
    private MathTransform reverseEngine;

    public ConvertSRID() {
        try {
            // reference : http://www.gisdeveloper.co.kr/?p=8942
            sourceCrs = CRS.decode("EPSG:4326");
            targetCrs = CRS.decode("EPSG:5179");
            engine = CRS.findMathTransform(sourceCrs, targetCrs, true);
            reverseEngine = CRS.findMathTransform(targetCrs, sourceCrs, true);
        } catch (FactoryException e) {
            e.printStackTrace();
        }
    }

    public Coordinate convertCoordinate(double longitude, double latitude) throws TransformException {
        DirectPosition2D source = new DirectPosition2D(sourceCrs, longitude, latitude);
        DirectPosition target = new DirectPosition2D(targetCrs);
        engine.transform(source, target);
        Coordinate coord = new Coordinate(target.getCoordinate()[1], target.getCoordinate()[0], 0);
        return coord;
    }

    public Coordinate revertCoordinate(double latitude, double longitude) throws TransformException {
        DirectPosition2D source = new DirectPosition2D(sourceCrs, latitude, longitude);
        DirectPosition target = new DirectPosition2D(sourceCrs);
        reverseEngine.transform(source, target);
        Coordinate coord = new Coordinate(target.getCoordinate()[0], target.getCoordinate()[1]);
        return coord;
    }
}

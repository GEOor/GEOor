package geo.hs.geoUtil;

import geo.hs.model.hillshade.Hillshade;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.io.WKBWriter;

public class WKB {

    private final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    private final WKBWriter wkbWriter = new WKBWriter();
    private final SRID srid = new SRID();

    public byte[] convertPolygonWKB(Hillshade hillShade) {
        double x1 = hillShade.getGrid().get(0);
        double y1 = hillShade.getGrid().get(1);
        double x2 = hillShade.getGrid().get(2);
        double y2 = hillShade.getGrid().get(3);

        Coordinate coord1 = srid.convertPoint(x1, y1);
        Coordinate coord2 = srid.convertPoint(x1, y2);
        Coordinate coord3 = srid.convertPoint(x2, y2);
        Coordinate coord4 = srid.convertPoint(x2, y1);

        Coordinate[] coords =
            new Coordinate[]{coord1, coord2, coord3, coord4, coord1};
        LinearRing ring = geometryFactory.createLinearRing(coords);
        LinearRing holes[] = null;

        return wkbWriter.write(geometryFactory.createPolygon(ring, holes));
    }
}


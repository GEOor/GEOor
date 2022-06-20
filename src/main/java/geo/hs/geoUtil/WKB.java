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

    public byte[] convertPolygonWKB(Hillshade hillShade) {
        double lat1 = hillShade.getGrid().get(0);
        double lon1 = hillShade.getGrid().get(1);
        double lat2 = hillShade.getGrid().get(2);
        double lon2 = hillShade.getGrid().get(3);

        Coordinate coord1 = new Coordinate(lon1, lat1);
        Coordinate coord2 = new Coordinate(lon1, lat2);
        Coordinate coord3 = new Coordinate(lon2, lat2);
        Coordinate coord4 = new Coordinate(lon2, lat1);

        Coordinate[] coords =
            new Coordinate[]{coord1, coord2, coord3, coord4, coord1};
        LinearRing ring = geometryFactory.createLinearRing(coords);
        LinearRing holes[] = null;

        return wkbWriter.write(geometryFactory.createPolygon(ring, holes));
    }
}


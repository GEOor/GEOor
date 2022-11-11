package geo.hs.repository;

import geo.hs.geoUtil.WKB;
import geo.hs.model.hillshade.Hillshade;
import geo.hs.model.road.Road;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RoadRepository {

    private final JdbcTemplate jdbcTemplate;
    private final WKB wkb = new WKB();
    private final String sql = "SELECT origin_id FROM road_divide WHERE ST_Intersects(st_setSRID(? ::geometry, 4326), the_geom) and sig_cd = ?";

    public RoadRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void findByGeom(HashMap<Integer, Road> roadHashMap, List<Hillshade> hillShades, int cityId) {
        for (Hillshade hillShade : hillShades) {
            jdbcTemplate.query(sql, new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    int roadId = rs.getInt(1);
                    if (!roadHashMap.containsKey(roadId)) {
                        roadHashMap.put(roadId, new Road(roadId));
                    }
                    roadHashMap.get(roadId).interSects(hillShade.getHillshade().intValue());
                    return 0;
                }
            }, wkb.convertPolygonWKB(hillShade), cityId);
        }
    }

    public void updateHillShade(Road road) {
        String sql = "UPDATE road SET hillshade = ? WHERE id = ?";
        jdbcTemplate.update(sql, road.getHillShadeAverage(), road.getId());
    }
}

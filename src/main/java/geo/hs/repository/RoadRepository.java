package geo.hs.repository;

import geo.hs.geoUtil.WKB;
import geo.hs.model.hillshade.HillShade;
import geo.hs.model.road.Road;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RoadRepository {

    private final JdbcTemplate jdbcTemplate;
    private final WKB wkb = new WKB();

    public RoadRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void findByGeom(HashMap<Integer, Road> roadHashMap, HillShade hillShades) {
        String sql = "SELECT id FROM road WHERE ST_Overlaps(?, geom)";
        jdbcTemplate.query(sql, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                int roadId = rs.getInt(1);
                if (roadHashMap.containsKey(roadId)) {
                    roadHashMap.put(roadId, new Road(roadId));
                }
                roadHashMap.get(roadId).overlapsGrid(hillShades.getHillshade().intValue());
                return 0;
            }
        }, wkb.convertPolygonWKB(hillShades));
    }

    public void updateHillShade(Road road) {
        String sql = "UPDATE road SET hillshade = ? WHERE id = ?";
        jdbcTemplate.update(sql, road.getHillShadeAverage(), road.getId());
    }
}

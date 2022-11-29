package geo.hs.repository;

import com.uber.h3core.H3Core;
import geo.hs.geoUtil.WKB;
import geo.hs.model.hillshade.Hillshade;
import geo.hs.model.road.Road;
import java.io.IOException;
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
    private H3Core h3;

    public RoadRepository(DataSource dataSource) throws IOException {
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.h3 = H3Core.newInstance();
    }

    public void findByGeom(HashMap<Integer, Road> roadHashMap, List<Hillshade> hillShades, int cityId) {
        String sql = "SELECT r.origin_id \n"
            + "FROM road_segment as r, testdsm as d\n"
            + "WHERE ST_Intersects(d.the_geom, r.the_geom) \n"
            + "and r.sig_cd = ? \n"
            + "and d.sig_cd = ? \n"
            + "and d.address = ?";
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
            }, cityId, cityId, hillShade.getAddress());
        }
    }

    public void updateHillShade(Road road) {
        String sql = "UPDATE road SET hillshade = ? WHERE id = ?";
        jdbcTemplate.update(sql, road.getHillShadeAverage(), road.getId());
    }
}

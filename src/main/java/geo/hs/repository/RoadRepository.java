package geo.hs.repository;

import com.uber.h3core.H3Core;
import geo.hs.geoUtil.WKB;
import geo.hs.model.hillshade.HillShade;
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

    public void findByGeom(HashMap<Integer, Road> roadHashMap, List<HillShade> hillShades) {
        String sql = "SELECT road_id \n"
            + "FROM hexagon_road \n"
            + "WHERE hexagon_id = ?";
        for (HillShade hillShade : hillShades) {
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
            }, hillShade.getAddress());
        }
    }

    public void updateHillShade(Road road) {
        String sql = "UPDATE road SET hillshade = ? WHERE id = ?";
        jdbcTemplate.update(sql, road.getHillShadeAverage(), road.getId());
    }
}

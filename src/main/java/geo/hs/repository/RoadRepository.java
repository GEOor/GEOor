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
    private H3Core h3;

    public RoadRepository(DataSource dataSource) throws IOException {
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.h3 = H3Core.newInstance();
    }


    public void updateHillShade(Hillshade dsm, int cityId) {
        String sql = "UPDATE testdsm SET hillshade = ? WHERE address = ? and sig_cd = ?";
        jdbcTemplate.update(sql, dsm.getHillshade(), h3.latLngToCell(dsm.getX(), dsm.getY(), 9), cityId);
    }
}

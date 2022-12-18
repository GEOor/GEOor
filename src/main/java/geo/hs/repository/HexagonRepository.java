package geo.hs.repository;

import com.uber.h3core.H3Core;
import geo.hs.model.dsm.Hexagon;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class HexagonRepository {
	
	private JdbcTemplate jdbcTemplate;
	private H3Core h3;
	
	@Autowired
	public void setDataSource(DataSource dataSource) throws IOException {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.h3 = H3Core.newInstance();
	}
	
	public List<Hexagon> getHexagon(Map<Long, Hexagon> hexagonMap, int cityId){
		String query = "SELECT h.id, h.height"
			+ " FROM hexagon as h, hexagon_admin as a"
			+ " where a.sig_cd = ? and h.id = a.hexagon_id";
		return this.jdbcTemplate.query(query,
				(rs, rowNum) -> hexagonMap.put(
					rs.getLong(1),
					new Hexagon(rs.getInt(2), 0)),
				cityId
		);
	}
}

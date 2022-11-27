package geo.hs.repository;

import com.uber.h3core.H3Core;
import geo.hs.model.dsm.Dsm;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GetDsmRepository {
	
	private JdbcTemplate jdbcTemplate;
	private H3Core h3;
	
	@Autowired
	public void setDataSource(DataSource dataSource) throws IOException {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.h3 = H3Core.newInstance();
	}
	
	public List<Dsm> getDsm(int cityId){
		// where 문의 city 부분은 db의 컬럼에 따라 변경
		String query = "select address, height from testdsm where sig_cd = ?";
		return this.jdbcTemplate.query(query,
				(rs, rowNum) -> new Dsm(
						rs.getLong(1),
						String.valueOf(h3.cellToLatLng(rs.getLong(1)).lat),
						String.valueOf(h3.cellToLatLng(rs.getLong(1)).lng),
						Double.parseDouble(String.valueOf(rs.getInt(2))),
						0),
				cityId
		);
	}
}

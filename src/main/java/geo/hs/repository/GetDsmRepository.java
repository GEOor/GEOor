package geo.hs.repository;

import geo.hs.model.dsm.Dsm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GetDsmRepository {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public List<Dsm> getDsm(int cityId){
		// where 문의 city 부분은 db의 컬럼에 따라 변경
		String query = "select * from dsm where sig_cd = ?";
		return this.jdbcTemplate.query(query,
				(rs, rowNum) -> new Dsm(
						rs.getString("x"),
						rs.getString("y"),
						rs.getDouble("z"),
						0),
				cityId
		);
	}
}

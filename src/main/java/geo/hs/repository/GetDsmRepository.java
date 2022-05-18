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
	
	public List<Dsm> getDsm(int did){
		String query = "select * from DSM where did = ?";
		return this.jdbcTemplate.query(query,
				(rs, rowNum) -> new Dsm(
						rs.getDouble("x"),
						rs.getDouble("y"),
						rs.getDouble("z"),
						0),
				did
		);
	}

	public ArrayList<ArrayList<Dsm>> dsm2DConverter(List<Dsm> dsms) {

		ArrayList<ArrayList<Dsm>> arr = new ArrayList<ArrayList<Dsm>>();

		double prev_x = -1;

		ArrayList<Dsm> dsmArrayList = new ArrayList<Dsm>();

		for(Dsm dsm : dsms) {
			if(dsm.getX() != prev_x) {
				arr.add(dsmArrayList);
				dsmArrayList = new ArrayList<Dsm>();
			}
			dsmArrayList.add(dsm);
		}

		return arr;

	}
	
}

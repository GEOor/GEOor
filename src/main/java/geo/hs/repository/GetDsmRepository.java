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
		String query = "select * from DSM where city = ?";
		return this.jdbcTemplate.query(query,
				(rs, rowNum) -> new Dsm(
						rs.getString("x"),
						rs.getString("y"),
						rs.getDouble("z"),
						0),
				cityId
		);
	}
	
	public List<Dsm> getAllDsm(){
		/*List<Dsm> ret = new ArrayList<>();
		for(int i=0; i<20000000; i+=1000000){ // 100만 단위로 끊어서 가지고 오고, List 병합, 각 지역별 최대 DSM 개수 구해서 조건문 변경하기
			String query = "select * from DSM ORDER BY x asc, y asc OFFSET ? LIMIT 1000000";
			List<Dsm> temp = this.jdbcTemplate.query(query,
					(rs, rowNum) -> new Dsm(
							rs.getString("x"),
							rs.getString("y"),
							rs.getDouble("z"),
							0),
					i
			);
			if(temp.isEmpty()) break; // 더 이상 가져오는 것이 없다면 break
			ret.addAll(temp);
		}
		return ret;*/
		// where 문의 city 부분은 db의 컬럼에 따라 변경
		String query = "select * from DSM";
		return this.jdbcTemplate.query(query,
				(rs, rowNum) -> new Dsm(
						rs.getString("x"),
						rs.getString("y"),
						rs.getDouble("z"),
						0)
		);
	}
	
}

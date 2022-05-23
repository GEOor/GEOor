package geo.hs.repository;

import geo.hs.model.Frozen.Frozen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FrozenRepository {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	/**
	 * DB에 있을때의 코드
	 */
	/*public List<Frozen> getFrozenRoad(){
		String query = "SELECT lat, lng FROM FROZEN";
		return this.jdbcTemplate.query(query,
				(rs, rowNum) -> new Frozen(
						rs.getDouble("lat"),
						rs.getDouble("lng"))
		);
	}*/
	
	/**
	 * txt 파일로 가지고 있기
	 * @return List<Frozen>
	 */
	public List<Frozen> getFrozenRoad(){
		List<Frozen> ret = new ArrayList<>();


		try {
			//파일 객체 생성
			File file = new File("/Users/suhwan/Desktop/geo&/GEOor/src/main/resources/files/상습결빙구간.txt");
			//입력 스트림 생성
			FileReader file_reader = new FileReader(file);
			
			int cur = 0;
			String temp = "";
			double lat = 0.0, lng = 0.0;
			boolean flag = true;
			
			while ((cur = file_reader.read()) != -1) {
				char c = (char) cur;
				if (c == ' ' || c == '	' || c == '\n') {
					if (flag) {
						lat = Double.valueOf(temp);
						flag = false;
					} else {
						lng = Double.valueOf(temp);
						ret.add(new Frozen(lat, lng));
						flag = true;
					}
					temp = "";
				} else temp += c;
			}
			file_reader.close();
		} catch (FileNotFoundException e) {
			e.getStackTrace();
		} catch (IOException e) {
			e.getStackTrace();
		}
		
		return ret;
	}
}

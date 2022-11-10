package geo.hs.service;

import geo.hs.model.dsm.Dsm;
import geo.hs.repository.GetDsmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class DsmService {
	
	private final GetDsmRepository getDsmRepository;
	
	@Autowired
	public DsmService(GetDsmRepository getDsmRepository) {
		this.getDsmRepository = getDsmRepository;
	}
	
	public List<Dsm> getDsm(int cityId){
		// 만약 cityId가 길게 넘어온다면 parsing Code 짜기
		List<Dsm> dsm = getDsmRepository.getDsm(cityId);
		//Collections.sort(dsm);
		return dsm;
	}
	
	public ArrayList<ArrayList<Dsm>> dsm2DConverter(List<Dsm> dsms) {

		// 1. List를 돌면서, 나왔던 x, y 좌표 각각을 기록

		//y, x 좌표 하나씩만 들어가게 map 처리
		HashMap<String, Integer> mapX = new HashMap<String, Integer>();
		HashMap<String, Integer> mapY = new HashMap<String, Integer>();
		HashMap<String, Dsm> mapXY = new HashMap<String, Dsm>();

		// y, x 좌표 들어갈 리스트
		ArrayList<Double> y = new ArrayList<Double>();
		ArrayList<Double> x = new ArrayList<Double>();

		for(Dsm dsm : dsms) {
			String dsmX = dsm.getX();
			String dsmY = dsm.getY();

			//map에 없었던 것들 추가
			if(!mapX.containsKey(dsmX)) {
				mapX.put(dsmX, 1);
				x.add(Double.parseDouble(dsmX));
			}

			if(!mapY.containsKey(dsmY)) {
				mapY.put(dsmY, 1);
				y.add(Double.parseDouble(dsmY));
			}

			mapXY.put(dsmY + dsmX, dsm);
		}

		// y, x 좌표 순서대로 정렬 (y축은 내림차순으로, x축은 오름차순으로)
		Collections.sort(y, Collections.reverseOrder()); Collections.sort(x);

		// 2. 나왔던 x, y 좌표 수 만큼의 2차원 List를 생성한다.
		ArrayList<ArrayList<Dsm>> arr = new ArrayList<ArrayList<Dsm>>();

		// 3. 배열을 돌면서, mapXY에 있는 좌표는 그대로 넣고, 없는 좌표들은 z 값을 -1로 넣어둔다.
		// z가 -1인 값은 이후 hillshade 계산이 끝난 후 버린다.
		for(Double iter_y : y) {
			ArrayList<Dsm> dsmArrayList = new ArrayList<Dsm>();
			for(Double iter_x: x) {
				if(mapXY.containsKey(iter_y.toString() + iter_x.toString())) {
					dsmArrayList.add(mapXY.get(iter_y.toString() + iter_x.toString()));
				} else {
					dsmArrayList.add(new Dsm("1", "1", -1, -1));
				}
			}
			arr.add(dsmArrayList);
		}
		
		return arr;

	}
	
	public List<Dsm> getAllDsm(){
		// 만약 cityId가 길게 넘어온다면 parsing Code 짜기
		List<Dsm> dsm = null;
		/*ArrayList<Dsm> dsm = new ArrayList<>();
		dsm.addAll(getDsmRepository.getAllDsm());*/
		// Collections.sort(dsm);
		return dsm;
	}
}

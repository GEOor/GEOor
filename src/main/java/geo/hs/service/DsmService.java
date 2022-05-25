package geo.hs.service;

import geo.hs.model.dsm.Dsm;
import geo.hs.repository.GetDsmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DsmService {
	
	private final GetDsmRepository getDsmRepository;
	
	@Autowired
	public DsmService(GetDsmRepository getDsmRepository) {
		this.getDsmRepository = getDsmRepository;
	}
	
	public List<Dsm> getDsm(String cityId){
		// 만약 cityId가 길게 넘어온다면 parsing Code 짜기
		List<Dsm> dsm = getDsmRepository.getDsm(Integer.valueOf(cityId));
		Collections.sort(dsm);
		return dsm;
	}
	
	public ArrayList<ArrayList<Dsm>> dsm2DConverter(List<Dsm> dsms) {
		
		ArrayList<ArrayList<Dsm>> arr = new ArrayList<ArrayList<Dsm>>();
		
		double prev_x = -1;
		
		ArrayList<Dsm> dsmArrayList = new ArrayList<Dsm>();
		
		for(Dsm dsm : dsms) {
			if(Double.valueOf(dsm.getX()) != prev_x) {
				arr.add(dsmArrayList);
				dsmArrayList = new ArrayList<Dsm>();
			}
			dsmArrayList.add(dsm);
		}
		
		return arr;
		
	}
}

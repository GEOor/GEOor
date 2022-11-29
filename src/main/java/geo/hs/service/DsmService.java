package geo.hs.service;

import geo.hs.model.dsm.Hexagon;
import geo.hs.repository.GetDsmRepository;
import java.util.Map;
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
	
	public List<Hexagon> getDsm(Map<Long, Hexagon> hexagonMap, int cityId){
		// 만약 cityId가 길게 넘어온다면 parsing Code 짜기
		List<Hexagon> hexagon = getDsmRepository.getDsm(hexagonMap, cityId);
		//Collections.sort(hexagon);
		return hexagon;
	}
}

package geo.hs.service;

import geo.hs.model.dsm.Hexagon;
import geo.hs.repository.HexagonRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HexagonService {
	
	private final HexagonRepository hexagonRepository;
	
	@Autowired
	public HexagonService(HexagonRepository hexagonRepository) {
		this.hexagonRepository = hexagonRepository;
	}
	
	public List<Hexagon> getHexagon(Map<Long, Hexagon> hexagonMap, int cityId){
		List<Hexagon> hexagon = hexagonRepository.getHexagon(hexagonMap, cityId);
		return hexagon;
	}
}

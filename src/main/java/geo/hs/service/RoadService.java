package geo.hs.service;

import geo.hs.model.hillshade.HillShade;
import geo.hs.model.road.Road;
import geo.hs.repository.RoadRepository;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoadService {

    private final RoadRepository roadRepository;
    private final HashMap<Integer, Road> roadHashMap = new HashMap<>();

    public void calcRoadHillShade(List<HillShade> hillShades, int cityId) {
        roadHashMap.clear();
        roadRepository.findByGeom(roadHashMap, hillShades, cityId);
    }

    public void updateRoadHillShade() {
        for (Road value : roadHashMap.values()) {
            roadRepository.updateHillShade(value);
        }
    }
}

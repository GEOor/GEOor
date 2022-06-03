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

    public void updateRoadHillShade(List<HillShade> hillShades) {
        for (HillShade hillShade : hillShades) {
            roadRepository.findByGeom(roadHashMap, hillShade);
        }
        for (Road value : roadHashMap.values()) {
            roadRepository.updateHillShade(value);
        }
    }
}

package geo.hs.service;

import geo.hs.model.hillshade.Hillshade;
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

    public void calcRoadHillShade(List<Hillshade> hillShades) {
        roadHashMap.clear();
        for (Hillshade hillShade : hillShades) {
            roadRepository.findByGeom(roadHashMap, hillShade);
        }
    }

    public void updateRoadHillShade() {
        for (Road value : roadHashMap.values()) {
            roadRepository.updateHillShade(value);
        }
    }

    public void test() {
        int count = 0;
        for (Road value : roadHashMap.values()) {
            count += value.getIntersectCount();
        }
        System.out.println(count);
    }
}

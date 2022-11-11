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

    public void calcRoadHillShade(List<Hillshade> hillShades, int cityId) {
        roadHashMap.clear();
        roadRepository.findByGeom(roadHashMap, hillShades, cityId);
    }

    public void updateRoadHillShade() {
        for (Road value : roadHashMap.values()) {
            roadRepository.updateHillShade(value);
        }
    }

    /**
     * 테스트 용도로만 써야 한다
     * 즉, 배포할 때는 이 함수 호출을 하면 안 됨
     */
    public void printResult() {
        int count = 0;
        for (Road value : roadHashMap.values()) {
            count += value.getIntersectCount();
        }
        System.out.println(count);
    }
}

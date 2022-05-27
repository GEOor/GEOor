package geo.hs.service;

import geo.hs.repository.RoadRepository;
import org.springframework.stereotype.Service;

@Service
public class RoadService {

    private final RoadRepository roadRepository;

    public RoadService(RoadRepository roadRepository) {
        this.roadRepository = roadRepository;
    }

    public void getRoads() {
        roadRepository.getRoads();
    }

    public void findOverlapsRoad() {
        roadRepository.overlapsByGeom();
    }

    public void updateRoadHillShade() {
        roadRepository.updateHillShade();
    }
}

package geo.hs.service;

import com.uber.h3core.H3Core;
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

    public void calcRoadHillShade(List<Hillshade> hillShades, int cityId) {
        for (Hillshade hillShade : hillShades) {
            roadRepository.updateHillShade(hillShade, cityId);
        }

    }
}

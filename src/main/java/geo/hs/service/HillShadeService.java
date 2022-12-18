package geo.hs.service;

import geo.hs.algorithm.hillshade.HillshadeAlgorithm;
import geo.hs.model.dsm.Hexagon;
import geo.hs.model.hillshade.HillShade;
import geo.hs.model.sun.SunInfo;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class HillShadeService {

    HillshadeAlgorithm hillshadeAlgorithm = new HillshadeAlgorithm();

    public ArrayList<HillShade> run(Map<Long, Hexagon> hexagonMap, SunInfo sunInfo){
        ArrayList<HillShade> hillShades = hillshadeAlgorithm.hsConverter(hexagonMap, sunInfo);
        return hillShades;
    }
}
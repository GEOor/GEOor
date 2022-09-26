package geo.hs.service;

import geo.hs.algorithm.hillshade.HillshadeAlgorithm;
import geo.hs.model.dsm.Dsm;
import geo.hs.model.hillshade.Hillshade;
import geo.hs.model.sun.SunInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class HillShadeService {

    HillshadeAlgorithm hillshadeAlgorithm = new HillshadeAlgorithm();

    public ArrayList<Hillshade> run(ArrayList<ArrayList<Dsm>> dsm2DArr, SunInfo sunInfo){

        ArrayList<Hillshade> hillShades = hillshadeAlgorithm.hsConverter(sunInfo, dsm2DArr);

        return hillShades;
    }
}
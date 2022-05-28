package geo.hs.service;

import geo.hs.algorithm.hillshade.HillshadeAlgorithm;
import geo.hs.model.dsm.Dsm;
import geo.hs.model.hillshade.HillShade;
import geo.hs.model.sun.SunInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class HillShadeService {

    HillshadeAlgorithm hillshadeAlgorithm = new HillshadeAlgorithm();

    public ArrayList<HillShade> run(ArrayList<ArrayList<Dsm>> dsm2DArr, SunInfo sunInfo){

        ArrayList<HillShade> hillShadeArr = new ArrayList<HillShade>();
        ArrayList<ArrayList<HillShade>> hs2DArr = hillshadeAlgorithm.hsConverter(sunInfo, dsm2DArr);
        ArrayList<HillShade> hillShades = new ArrayList<HillShade>();

        for(ArrayList<HillShade> hs1DArr : hs2DArr) {
            hillShades.addAll(hs1DArr);
        }

        return hillShades;
    }
}
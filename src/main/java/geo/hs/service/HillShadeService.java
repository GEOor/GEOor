package geo.hs.service;

import geo.hs.algorithm.hillshade.HillshadeAlgorithm;
import geo.hs.model.dsm.Dsm;
import geo.hs.model.hillshade.Hillshade;
import geo.hs.model.sun.SunInfo;

import java.util.ArrayList;
import java.util.List;

public class HillShadeService {

    HillshadeAlgorithm hillshadeAlgorithm = new HillshadeAlgorithm();

    public ArrayList<Hillshade> run(ArrayList<ArrayList<Dsm>> dsm2DArr, SunInfo sunInfo){

        ArrayList<Hillshade> hillshadeArr = new ArrayList<Hillshade>();
        ArrayList<ArrayList<Hillshade>> hs2DArr = hillshadeAlgorithm.hsConverter(sunInfo, dsm2DArr);
        ArrayList<Hillshade> hillshades = new ArrayList<Hillshade>();

        for(ArrayList<Hillshade> hs1DArr : hs2DArr) {
            hillshades.addAll(hs1DArr);
        }

        return hillshades;
    }
}
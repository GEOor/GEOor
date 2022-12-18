package geo.hs.algorithm.hillshade;

import com.uber.h3core.H3Core;
import geo.hs.model.dsm.Hexagon;
import geo.hs.model.hillshade.HillShade;
import geo.hs.model.sun.SunInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 내용 : HillShade Algorithm 구현, 태양고도각(si), dem값(di) 2차원 ArrayList를 이용해 똑같은 크기의 HillShade(음영기복도) 2차원 ArrayList 로 변환
 작동 원리
 1) i, j가 모두 0이 아닌 점에서, 해당 점을 가운데로하는 3 by 3 정사각형을 만든다.
 2) 이미 구한 태양고도각 정보들과, 주변 dem 값들을 이용해 공식에 대입
 3) HillShade ArrayList에 저장
 */
public class HillshadeAlgorithm {

    private H3Core h3;
    private final int gridDistance = 1;
    private final int cellSpacing = 10; // h3 res 값 반드시 모듈 레포에서 설정한 res 값과 같아야 한다.

    public HillshadeAlgorithm() {
        try {
            this.h3 = H3Core.newInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<HillShade> hsConverter(Map<Long, Hexagon> hexagonMap, SunInfo si){
        ArrayList<HillShade> hs = new ArrayList<>();

        Double Zenith_deg = 0.0, Zenith_rad = 0.0, Azimuth_math = 0.0, Azimuth_rad = 0.0;
        Double Slope_rad = 0.0, Aspect_rad = 0.0;
        double a, b, c, d, e, f, g;

        for (Map.Entry<Long, Hexagon> hexagon : hexagonMap.entrySet()) {
            long address = hexagon.getKey();
            List<Long> grids = h3.gridDisk(address, gridDistance);

            a = getHeight(hexagonMap, grids.get(0));
            b = getHeight(hexagonMap, grids.get(5));
            c = getHeight(hexagonMap, grids.get(4));
            d = getHeight(hexagonMap, grids.get(3));
            e = getHeight(hexagonMap, grids.get(2));
            f = getHeight(hexagonMap, grids.get(1));
            g = getHeight(hexagonMap, grids.get(6));

            if (a == -1 || b == -1 || c == -1 || d == -1 || e == -1 || f == -1 || g == -1) {
                continue;
            }

            double di = ((e - a) + (a - b)) / 2;
            double dk = ((g - a) + (a - d)) / 2;
            double dj = ((f - a) + (a - c)) / 2;
            double dy = di + (dj * Math.sin(Math.PI / 6)) - (dk * Math.sin(Math.PI / 6));
            double dx = (dj * Math.cos(Math.PI / 6)) + (dk * Math.cos(Math.PI / 6));

            // (2) Zenith_deg = 90 - Altitude
            Zenith_deg = 90 - si.getAltitude();

            // (3) Zenith_rad = Zenith * pi / 180.0
            Zenith_rad = (Zenith_deg * Math.PI) / 180.0;

            // (4) Azimuth_math = 360.0 - Azimuth + 90
            Azimuth_math = 360.0 - si.getAzimuth();

            // (5) if Azimuth_math >= 360.0, then: Azimuth_math = Azimuth_math - 360.0
            if(Azimuth_math >= 360.0)
                Azimuth_math = Azimuth_math - 360.0;

            // (6) Azimuth_rad = Azimuth_math * pi / 180.0
            Azimuth_rad = (Azimuth_math * Math.PI) / 180.0;

            // (9) Slope_rad = ATAN (z_factor * √ ([dx]2 + [dy]2) / cell spacing)
            Slope_rad = Math.atan(Math.sqrt(dx * dx + dy * dy) / cellSpacing);

            Aspect_rad = 0.0;

            // (10.1) if derivative_x is non-zero
            if(dx != 0.0) {
                Aspect_rad = Math.atan2(dy, -dx);
                // (10.2) if Aspect_rad < 0 then Aspect_rad = 2 * pi + Aspect_rad
                if(Aspect_rad < 0) Aspect_rad = 2 * Math.PI + Aspect_rad;
            }

            // (10.3) if derivative_x is zero
            if(dx == 0.0){
                if(dy > 0) Aspect_rad = Math.PI / 2;
                else if(dy < 0) Aspect_rad = 2 * Math.PI - (Math.PI / 2);
                else Aspect_rad = Azimuth_rad;
            }

            double hillShade = 255.0 * (
                (Math.cos(Zenith_rad)
                    * Math.cos(Slope_rad))
                    + (Math.sin(Zenith_rad)
                    * Math.sin(Slope_rad)
                    * Math.cos(Azimuth_rad - Aspect_rad)));

            if(hillShade < 0)
                hillShade = 0.0;
            hs.add(new HillShade(address, hillShade));
        }
        return hs;

    }

    private long getHeight(Map<Long, Hexagon> hexagonMap, long address) {
        if (hexagonMap.containsKey(address)) {
            return hexagonMap.get(address).getHeight();
        }
        return -1;
    }
}
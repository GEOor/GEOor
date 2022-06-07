package geo.hs.algorithm.hillshade;

import geo.hs.model.dsm.Dsm;
import geo.hs.model.hillshade.Hillshade;
import geo.hs.model.sun.SunInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 내용 : HillShade Algorithm 구현, 태양고도각(si), dem값(di) 2차원 ArrayList를 이용해 똑같은 크기의 HillShade(음영기복도) 2차원 ArrayList 로 변환
 작동 원리
 1) i, j가 모두 0이 아닌 점에서, 해당 점을 가운데로하는 3 by 3 정사각형을 만든다.
 2) 이미 구한 태양고도각 정보들과, 주변 dem 값들을 이용해 공식에 대입
 3) HillShade ArrayList에 저장
 */
public class HillshadeAlgorithm {
    public ArrayList<Hillshade> hsConverter(SunInfo si, ArrayList<ArrayList<Dsm>> di){
        ArrayList<Hillshade> hs = new ArrayList<Hillshade>();
    
        int cnt = 0, flag = 0;
        Double Zenith_deg = 0.0, Zenith_rad = 0.0, Azimuth_math = 0.0, Azimuth_rad = 0.0;
        Double derivative_x = 0.0, derivative_y = 0.0, Slope_rad = 0.0, Aspect_rad = 0.0, hs_cell = 0.0;
        Hillshade hs_xy;
        ArrayList<Double> tmp_grid;
        HashMap<Character, Double>windows;
    
        for(int i=1; i<di.size() - 1; i++){

            for(int j=1; j<di.get(i).size() - 1; j++){
                /*
                3x3 window
                (a, b, c)
                (d, e, f) ---> e = (i, j) 좌표의 음영기복도
                (g, h, i)
                 */
                windows = new HashMap<Character, Double>();
                cnt = 0;

                //만약 9개 칸에 z값이 -1인 경우, hillshade를 정상적으로 계산할 수 없다. 그 부분 체크
                flag = 0;

                for(int y=i-1; y<=i+1; y++){
                    for(int x=j-1; x<=j+1; x++){
                        windows.put((char) ('a' + cnt), di.get(y).get(x).getZ());
                        cnt++;
                        if(di.get(y).get(x).getZ() == -1) flag = 1;
                    }
                }

                if(flag == 1) continue;

                // (2) Zenith_deg = 90 - Altitude
                Zenith_deg = 90 - si.getAltitude();

                // (3) Zenith_rad = Zenith * pi / 180.0
                Zenith_rad = (Zenith_deg * Math.PI) / 180.0;

                // (4) Azimuth_math = 360.0 - Azimuth + 90
                Azimuth_math = 360.0 - si.getAzimuth();

                // (5) if Azimuth_math >= 360.0, then: Azimuth_math = Azimuth_math - 360.0
                if(Azimuth_math >= 360.0) Azimuth_math = Azimuth_math - 360.0;

                // (6) Azimuth_rad = Azimuth_math * pi / 180.0
                Azimuth_rad = (Azimuth_math * Math.PI) / 180.0;

                // The rate of change in the x direction for cell e is calculated with the following algorithm
                // (7) [dz/dx] = ((c + 2f + i) - (a + 2d + g)) / (8 * cellsize)
                derivative_x = ((windows.get('c') + 2 * windows.get('f') + windows.get('i')) -
                        (windows.get('a') + 2 * windows.get('d') + windows.get('g'))) / (8 * 5);

                // The rate of change in the y direction for cell 'e' is calculated with the following algorithm
                // (8) [dz/dy] = ((g + 2h + i) - (a + 2b + c)) / (8 * cellsize)
                derivative_y = ((windows.get('g') + 2 * windows.get('h') + windows.get('i')) -
                        (windows.get('a') + 2 * windows.get('b') + windows.get('c'))) / (8 * 5);

                // The steepest downhill descent from each cell in the surface is the slope.
                // The algorithm for calculating the slope in radians, incorporating the z-factor, is
                // (9) Slope_rad = ATAN (z_factor * √ ([dz/dx]2 + [dz/dy]2))
                Slope_rad = Math.atan(Math.sqrt(derivative_x * derivative_x + derivative_y * derivative_y));

                Aspect_rad = 0.0;

                // (10.1) if derivative_x is non-zero
                if(derivative_x != 0.0) {
                    Aspect_rad = Math.atan2(derivative_y, -derivative_x);
                    // (10.2) if Aspect_rad < 0 then Aspect_rad = 2 * pi + Aspect_rad
                    if(Aspect_rad < 0) Aspect_rad = 2 * Math.PI + Aspect_rad;
                }

                // (10.3) if derivative_x is zero
                if(derivative_x == 0.0){
                    if(derivative_y > 0) Aspect_rad = Math.PI / 2;
                    else if(derivative_y < 0) Aspect_rad = 2 * Math.PI - (Math.PI / 2);
                    else Aspect_rad = Azimuth_rad;
                }

                hs_cell = 255.0 * ((Math.cos(Zenith_rad) * Math.cos(Slope_rad)) + (Math.sin(Zenith_rad) * Math.sin(Slope_rad)
                        * Math.cos(Azimuth_rad - Aspect_rad)));
    
                if(hs_cell < 0) hs_cell = 0.0;

                tmp_grid = new ArrayList<Double>();

                //왼쪽 위, 오른쪽 위, 오른쪽 아래, 왼쪽 아래 (한붓그리기 형태)
                for(int grid_y = i-1; grid_y <= i+1; grid_y +=2){
                    if(grid_y == i-1) {
                        for (int grid_x = j - 1; grid_x <= j + 1; grid_x += 22) {
                            tmp_grid.add(Double.parseDouble(di.get(grid_y).get(grid_x).getY()));
                            tmp_grid.add(Double.parseDouble(di.get(grid_y).get(grid_x).getX()));
                        }
                    }
                    else {
                        for (int grid_x = j + 1; grid_x >= j - 1; grid_x -= 22) {
                            tmp_grid.add(Double.parseDouble(di.get(grid_y).get(grid_x).getY()));
                            tmp_grid.add(Double.parseDouble(di.get(grid_y).get(grid_x).getX()));
                        }
                    }
                }

                hs_xy = new Hillshade(si.getX(), si.getY(), hs_cell, tmp_grid);

                hs.add(hs_xy);
            }
        }
        return hs;
    }
}
package geo.hs.model.hillshade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Hillshade {

    //x,y 좌표 값
    private Double x;
    private Double y;

    //위도, 경도
    private Double latitude, longitude;

    //hillshade 값
    private Double hillshade;

    //4방향 모서리 위,경도 값 (왼쪽 위, 아래, 오른쪽 위, 아래 순)
    private ArrayList<Double> grid = new ArrayList<Double>();

}

package geo.hs.model.sun;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class SunInfo {

    //x, y좌표
    private double x, y;
    //위도, 경도
    private Double latitude, longitude;
    //시각
    private Integer time;
    //방위각
    private Double azimuth;
    //고도
    private Double altitude;
    //적경
    private Double ascension;
    //적위
    private Double declination;
}

package geo.hs.model.shp;

import lombok.Getter;

@Getter
public class RoadStatus {
    String rbid;
    int count;
    double hillShade;

    public RoadStatus(String rbid, double hillShade) {
        this.rbid = rbid;
        this.count = 1;
        this.hillShade = hillShade;
    }

    public void addHillShade(double hillShade) {
        this.count++;
        this.hillShade += hillShade;
    }
}

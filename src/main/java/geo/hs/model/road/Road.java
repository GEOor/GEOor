package geo.hs.model.road;

import lombok.Getter;

@Getter
public class Road {
    String rbid;
    int count;
    double hillShade;

    public Road(String rbid, double hillShade) {
        this.rbid = rbid;
        this.count = 1;
        this.hillShade = hillShade;
    }

    public void addHillShade(double hillShade) {
        this.count++;
        this.hillShade += hillShade;
    }
}

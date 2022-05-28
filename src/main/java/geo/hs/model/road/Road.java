package geo.hs.model.road;

public class Road {

    private int id;
    private int overlapsCount;
    private int totalHillShadeSum;

    public Road(int id) {
        this.id = id;
        this.overlapsCount = 0;
        this.totalHillShadeSum = 0;
    }

    public void overlapsGrid(int hillShade) {
        overlapsCount++;
        totalHillShadeSum += hillShade;
    }

    public int getHillShadeAverage() {
        if(overlapsCount == 0 && totalHillShadeSum == 0) {
            return 0;
        }
        return totalHillShadeSum / overlapsCount;
    }

    public int getId() {
        return id;
    }
}

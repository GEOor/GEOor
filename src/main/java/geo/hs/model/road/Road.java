package geo.hs.model.road;

public class Road {

    private int id;
    private int interSectCount;
    private int totalHillShadeSum;

    public Road(int id) {
        this.id = id;
        this.interSectCount = 0;
        this.totalHillShadeSum = 0;
    }

    public void interSects(int hillShade) {
        interSectCount++;
        totalHillShadeSum += hillShade;
    }

    public int getHillShadeAverage() {
        if(interSectCount == 0 && totalHillShadeSum == 0) {
            return 0;
        }
        return totalHillShadeSum / interSectCount;
    }

    public int getId() {
        return id;
    }

    public int getIntersectCount() {
        return interSectCount;
    }

    public int getTotalHillShadeSum() {
        return totalHillShadeSum;
    }
}

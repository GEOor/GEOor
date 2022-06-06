package geo.hs.model.dsm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class Dsm  implements Comparable<Dsm>{
	
	private String x;
	
	private String y;
	
	private double z;
	
	private int hillShade;
	
	@Override
	public int compareTo(Dsm dsm) {
		if (Double.valueOf(dsm.x) < Double.valueOf(x)) return 1;
		else if (Double.valueOf(dsm.x) >= Double.valueOf(x)) {
			if(Double.valueOf(dsm.y) < Double.valueOf(y)) return 1;
			else if(Double.valueOf(dsm.y) > Double.valueOf(y)) return -1;
			return 0;
		}
		else return 0;
	}
}

package geo.hs.model.scheduler;

import geo.hs.model.sun.SunInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter @Setter
public class SchedulerSunInfo {
	
	private double lat;
	
	private double lng;
	
	private ArrayList<SunInfo> arr;
	
}

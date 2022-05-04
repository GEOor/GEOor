package geo.hs.model.scheduler;

import geo.hs.model.sun.SunInfo;
import lombok.AllArgsConstructor;

import java.util.ArrayList;

@AllArgsConstructor
public class SchedulerSunInfo {
	
	private double lat;
	
	private double lng;
	
	private ArrayList<SunInfo> arr;
	
}

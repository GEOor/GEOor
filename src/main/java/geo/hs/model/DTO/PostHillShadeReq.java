package geo.hs.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@AllArgsConstructor
@ToString
public class PostHillShadeReq {
	
	public String latitude;
	
	public String longitude;
	
	public String cityId;
	
	public String date;
	
	public String time;
	
}

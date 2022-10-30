package geo.hs.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostHillShadeReq {
	
	public String latitude;
	
	public String longitude;
	
	public String cityId;
	
}

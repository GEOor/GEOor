package geo.hs.model.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BasicDataReq {
    public String address;

    public String date;

    public String time;
}

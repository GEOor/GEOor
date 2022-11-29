package geo.hs.controller;

import geo.hs.crawling.Crawler;
import geo.hs.model.DTO.BasicDataReq;
import geo.hs.model.DTO.PostHillShadeReq;
import geo.hs.model.dsm.Hexagon;
import geo.hs.model.hillshade.HillShade;
import geo.hs.model.scheduler.SchedulerSunInfo;
import geo.hs.service.DsmService;
import geo.hs.service.HillShadeService;
import geo.hs.service.RoadService;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class HillShadeController {

	private final DsmService dsmService;
	private final HillShadeService hillShadeService;
	private final RoadService roadService;

	private Crawler crawler = new Crawler();
	private ArrayList<SchedulerSunInfo> ssi = new ArrayList<>();

	@Autowired
	public HillShadeController(DsmService dsmService, HillShadeService hillShadeService, RoadService roadService) {
		this.dsmService = dsmService;
		this.hillShadeService = hillShadeService;
		this.roadService = roadService;
	}

	/**
	 * www.vworld.kr에서 한글 주소를 보내주어서 위도, 경도, 도시 번호를 가지고 옴 (ex. 강동구 → 11740)
	 * @param req : address(한글 주소), date(현재 날짜), time(현재 시간)
	 * @return postHillShadeReq : latitude(위도), longitude(경도), cityId(도시 번호)
	 */
	@PostMapping("/hillShade")
	ResponseEntity<PostHillShadeReq> requestHillShade(@RequestBody BasicDataReq req) {
		try {
			URL url = new URL(this.getURL(req.getAddress()));

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json; utf-8");
			conn.setDoOutput(true);

			OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
			os.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			JSONObject jsonObject = (JSONObject) JSONValue.parse(in.readLine());

			in.close();
			conn.disconnect();

			PostHillShadeReq postHillShadeReq = this.jsonToDTO(jsonObject);

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");

			ResponseEntity<PostHillShadeReq> responseEntity = new ResponseEntity<>(postHillShadeReq, httpHeaders,
					HttpStatus.OK);

			this.updateHillShade(postHillShadeReq, req);

			return responseEntity;
		} catch (Exception e) {
			e.printStackTrace();
			ResponseEntity<PostHillShadeReq> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			return responseEntity;
		}
	}

	/**
	 * URL에 대한 자세한 내용은 https://www.vworld.kr/dev/v4dv_geocoderguide2_s001.do 를 참조
	 * @param address : 한글 주소(ex. 강동구)
	 * @return
	 */
	private String getURL(String address) {
		return "http://api.vworld.kr/req/address?service=address&request=getcoord&version=2.0"
				+ "&crs=epsg:3857"
				+ "&address=" + address + "&refine=true&simple=false&format=json&type=road"
				+ "&key=49EA5D21-2E61-3344-82B1-9E3F0B6C5805";
	}

	/**
	 * vworld api를 통해 가져온 JSON을 파싱하여 위도, 경도, 도시 번호를 가지고 옴
	 * @param obj
	 * @return
	 */
	private PostHillShadeReq jsonToDTO(JSONObject obj) {
		JSONObject response = (JSONObject) obj.get("response");
		JSONObject result = (JSONObject) response.get("result");
		JSONObject point = (JSONObject) result.get("point");
		JSONObject refined = (JSONObject) response.get("refined");
		JSONObject structure = (JSONObject) refined.get("structure");

		String lat = (String) point.get("x");
		String lng = (String) point.get("y");
		int cityId = Integer.parseInt(((String) structure.get("level4AC")).substring(0, 5));

		return PostHillShadeReq.builder()
				.latitude(lat)
				.longitude(lng)
				.cityId(cityId)
				.build();
	}

	void updateHillShade(PostHillShadeReq req, BasicDataReq basicDataReq) {
		String dateString = basicDataReq.getDate();
		String timeString = basicDataReq.getTime();

		// 태양고도각 크롤링
		// crawler 호출
		double lat = Double.parseDouble(req.getLatitude());
		double lng = Double.parseDouble(req.getLongitude());
		crawler.run(lat, lng, dateString); // 현재는 임시로 x, y = 0 으로 둠, HillShade 알고리즘과 맞춰봐야됨

		SchedulerSunInfo si = new SchedulerSunInfo(lat, lng, crawler.getSi());

		Map<Long, Hexagon> hexagonMap = new HashMap<>();
		// 해당 cityCode에 맞는 지역의 DSM 가져오기
		dsmService.getDsm(hexagonMap, req.getCityId());

		// 각 DSM 파일들 HillShade 계산
		// 특정 시간 데이터를 확인 (분은 필요 없음)
		int time = Integer.parseInt(timeString.split(":")[0]);
		ArrayList<HillShade> hs1DArr = hillShadeService.run(hexagonMap, si.getArr().get(time));
		log.info("hillshade update start");
		roadService.calcRoadHillShade(hs1DArr, req.getCityId());
		roadService.updateRoadHillShade();
		log.info("hillshade update end");
	}
}

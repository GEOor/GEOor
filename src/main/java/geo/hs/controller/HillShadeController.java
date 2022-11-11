package geo.hs.controller;

import geo.hs.crawling.Crawler;
import geo.hs.model.DTO.BasicDataReq;
import geo.hs.model.DTO.PostHillShadeReq;
import geo.hs.model.dsm.Dsm;
import geo.hs.model.hillshade.Hillshade;
import geo.hs.model.scheduler.SchedulerSunInfo;
import geo.hs.service.DsmService;
import geo.hs.service.HillShadeService;
import geo.hs.service.RoadService;
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

	private String getURL(String address) {
		return "http://api.vworld.kr/req/address?service=address&request=getcoord&version=2.0"
				+ "&crs=epsg:3857"
				+ "&address=" + address + "&refine=true&simple=false&format=json&type=road"
				+ "&key=49EA5D21-2E61-3344-82B1-9E3F0B6C5805";
	}

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

		// 해당 cityCode에 맞는 지역의 DSM 가져오기
		List<Dsm> dsms = dsmService.getDsm(req.getCityId());
		ArrayList<ArrayList<Dsm>> dsm2DArr = dsmService.dsm2DConverter(dsms);

		// 태양고도각 크롤링
		// crawler 호출
		double lat = Double.parseDouble(req.getLatitude());
		double lng = Double.parseDouble(req.getLongitude());
		crawler.run(lat, lng, dateString); // 현재는 임시로 x, y = 0 으로 둠, Hillshade 알고리즘과 맞춰봐야됨

		SchedulerSunInfo si = new SchedulerSunInfo(lat, lng, crawler.getSi());

		// 각 DSM 파일들 HillShade 계산
		int time = timeString.charAt(0) == '0' ? timeString.charAt(1) - '0' : Integer.parseInt(timeString);
		ArrayList<Hillshade> hs1DArr = hillShadeService.run(dsm2DArr, si.getArr().get(time));
		roadService.calcRoadHillShade(hs1DArr, req.getCityId());
		roadService.updateRoadHillShade();
		roadService.printResult();
	}
}

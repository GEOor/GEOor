package geo.hs.controller;

import geo.hs.crawling.Crawler;
import geo.hs.model.DTO.PostHillShadeReq;
import geo.hs.model.DTO.basicDataReq;
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

	@PostMapping("/requestHillShade")
	ResponseEntity<PostHillShadeReq> requestHillShade(@RequestBody basicDataReq req) {
		PostHillShadeReq postHillShadeReq = null;
		try {
			URL url = new URL("http://api.vworld.kr/req/address?service=address&request=getcoord&version=2.0"
					+ "&crs=epsg:3857"
					+ "&address=" + req.getAddress() + "&refine=true&simple=false&format=json&type=road"
					+ "&key=49EA5D21-2E61-3344-82B1-9E3F0B6C5805");

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
			System.out.println(jsonObject);
			JSONObject resJsonObject = (JSONObject) jsonObject.get("response");
			JSONObject resultJsonObject = (JSONObject) resJsonObject.get("result");
			JSONObject pointJsonObject = (JSONObject) resultJsonObject.get("point");
			JSONObject refinedJsonObject = (JSONObject) resJsonObject.get("refined");
			JSONObject structureJsonObject = (JSONObject) refinedJsonObject.get("structure");

			String lat = (String) pointJsonObject.get("x");
			String lng = (String) pointJsonObject.get("y");
			System.out.println(lat);
			System.out.println(lng);
			String cityId = ((String) structureJsonObject.get("level4AC")).substring(0, 5);
			System.out.println(cityId);

			System.out.println(lat + ", " + lng + ", " + cityId);

			postHillShadeReq = new PostHillShadeReq(lat, lng, cityId);

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");

			ResponseEntity<PostHillShadeReq> responseEntity = new ResponseEntity<>(postHillShadeReq, httpHeaders,
					HttpStatus.OK);

			this.updateHillShade(postHillShadeReq, req.getDate(), req.getTime());

			return responseEntity;
		} catch (Exception e) {
			e.printStackTrace();
			ResponseEntity<PostHillShadeReq> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			return responseEntity;
		}
	}

	void updateHillShade(PostHillShadeReq req, String dateString, String timeString) {
		// 해당 cityCode에 맞는 지역의 DSM 가져오기
		List<Dsm> dsms = dsmService.getDsm(req.cityId);
		ArrayList<ArrayList<Dsm>> dsm2DArr = dsmService.dsm2DConverter(dsms);

		// 태양고도각 크롤링
		// crawler 호출
		double lat = Double.parseDouble(req.getLatitude());
		double lng = Double.parseDouble(req.getLongitude());
		crawler.run(lat, lng, dateString); // 현재는 임시로 x, y = 0 으로 둠, hillshade 알고리즘과 맞춰봐야됨

		SchedulerSunInfo si = new SchedulerSunInfo(lat, lng, crawler.getSi());

		// 각 DSM 파일들 HillShade 계산
		int time = timeString.charAt(0) == '0' ? timeString.charAt(1) - '0' : Integer.parseInt(timeString);
		ArrayList<Hillshade> hs1DArr = hillShadeService.run(dsm2DArr, si.getArr().get(time));
		System.out.println("start");
		// 일정 크기의 HillShade 리스트에 대한 road HillShade 값 계산
		roadService.calcRoadHillShade(hs1DArr);
		// 최종 계산된 road HillShade 값 DB에 갱신
		roadService.updateRoadHillShade();
		roadService.test();
	}
}

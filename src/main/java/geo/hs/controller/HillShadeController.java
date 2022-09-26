package geo.hs.controller;

import geo.hs.crawling.Crawler;
import geo.hs.model.DTO.PostHillShadeReq;
import geo.hs.model.dsm.Dsm;
import geo.hs.model.hillshade.Hillshade;
import geo.hs.model.scheduler.SchedulerSunInfo;
import geo.hs.service.DsmService;
import geo.hs.service.HillShadeService;
import geo.hs.service.RoadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
	void updateHillShade(@RequestBody PostHillShadeReq req){
		// 해당 cityCode에 맞는 지역의 DSM 가져오기
		List<Dsm> dsms = dsmService.getDsm(req.cityId);
		ArrayList<ArrayList<Dsm>> dsm2DArr = dsmService.dsm2DConverter(dsms);
		
		// 태양고도각 크롤링
		// crawler 호출
		double lat = Double.parseDouble(req.getLatitude());
		double lng = Double.parseDouble(req.getLongitude());
		crawler.run(lat, lng, req.getDate()); // 현재는 임시로 x, y = 0 으로 둠, hillshade 알고리즘과 맞춰봐야됨
		
		SchedulerSunInfo si = new SchedulerSunInfo(lat, lng, crawler.getSi());
		
		// 각 DSM 파일들 HillShade 계산
		int time = req.getTime().charAt(0) == '0' ? req.getTime().charAt(1) - '0' : Integer.parseInt(req.getTime());
		ArrayList<Hillshade> hs1DArr = hillShadeService.run(dsm2DArr, si.getArr().get(time));
		System.out.println("start");
		// 일정 크기의 HillShade 리스트에 대한 road HillShade 값 계산
		roadService.calcRoadHillShade(hs1DArr);
		// 최종 계산된 road HillShade 값 DB에 갱신
		roadService.updateRoadHillShade();
		roadService.test();
	}
}

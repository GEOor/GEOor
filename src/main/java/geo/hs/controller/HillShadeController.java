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
		double lat = Double.valueOf(req.getLatitude());
		double lng = Double.valueOf(req.getLongitude());
		crawler.run(lat, lng, req.getDate()); // 현재는 임시로 x, y = 0 으로 둠, hillshade 알고리즘과 맞춰봐야됨
		
		SchedulerSunInfo si = new SchedulerSunInfo(lat, lng, crawler.getSi());
		
		// 각 DSM 파일들 HillShade 계산
		int time = req.getTime().charAt(0) == '0' ? req.getTime().charAt(1) - '0' : Integer.valueOf(req.getTime());
		ArrayList<Hillshade> hs1DArr = hillShadeService.run(dsm2DArr, si.getArr().get(time));
		System.out.println("start");
		// 일정 크기의 HillShade 리스트에 대한 road HillShade 값 계산
		roadService.calcRoadHillShade(hs1DArr);
		// 최종 계산된 road HillShade 값 DB에 갱신
		roadService.updateRoadHillShade();
		roadService.test();
	}
	
	@PostMapping("/test")
	void testHillShade(@RequestBody PostHillShadeReq req){
		// 모든 DSM 가져오는 코드 테스트
		long startTime = System.currentTimeMillis();
		/**
		 * 시군구 코드가 앞 2자리가 온다고 가정함
		 */
		List<Dsm> dsms = dsmService.getDsm(req.getCityId());
		long endTime = System.currentTimeMillis();
		log.info("전체 DSM을 가져오는 데 걸린 시간 = {} sec 입니다.", (endTime - startTime) / 1000);
		
		startTime = System.currentTimeMillis();
		ArrayList<ArrayList<Dsm>> dsm2DArr = dsmService.dsm2DConverter(dsms);
		endTime = System.currentTimeMillis();
		log.info("Dsm을 2D Arr로 변경하는데 걸리는 시간 = {} sec 입니다.", (endTime - startTime) / 1000);
		log.info("DSMS ARR 크기 = {}", dsms.size());
//		dsms = null;
		
		// 태양고도각 크롤링
		// crawler 호출
		double lat = Double.valueOf(req.getLatitude());
		double lng = Double.valueOf(req.getLongitude());
		log.info("lat = {}, lng = {}", lat, lng);
		
		log.info("Sun parameter Crawling Start!!!");
		startTime = System.currentTimeMillis();
		crawler.run(lat, lng, req.getDate()); // 현재는 임시로 x, y = 0 으로 둠, hillshade 알고리즘과 맞춰봐야됨
		endTime = System.currentTimeMillis();
		log.info("Sun parameter 크롤링에 걸린 시간 = {} sec 입니다.", (endTime - startTime) / 1000);
		
		SchedulerSunInfo si = new SchedulerSunInfo(lat, lng, crawler.getSi());
		
		// 각 DSM 파일들 HillShade 계산
		log.info("start calc hillshade!!!");
		startTime = System.currentTimeMillis();
		int time = req.getTime().charAt(0) == '0' ? req.getTime().charAt(1) - '0' : Integer.valueOf(req.getTime());
		log.info("time = {}", time);
		ArrayList<Hillshade> hs1DArr = hillShadeService.run(dsm2DArr, si.getArr().get(time));
		endTime = System.currentTimeMillis();
		log.info("hillShade 값을 계산하는데 걸린 시간 = {} sec 입니다.", (endTime - startTime) / 1000);

		startTime = System.currentTimeMillis();
		roadService.calcRoadHillShade(hs1DArr);
		endTime = System.currentTimeMillis();
		log.info("road hillshade 값을 계산하는데 걸린 시간 = {} sec 입니다.", (endTime - startTime) / 1000);

		startTime = System.currentTimeMillis();
		roadService.updateRoadHillShade();
		endTime = System.currentTimeMillis();
		log.info("road hillshade 값을 update 하는데 걸린 시간 = {} sec 입니다.", (endTime - startTime) / 1000);
		roadService.test();
	}
	
//	@PostMapping("/test")
	void testDivide(@RequestBody PostHillShadeReq req){
		long allStartTime, allEndTime;
		long startTime, endTime;
		
		allStartTime = System.currentTimeMillis();
		
		// 태양고도각 크롤링
		// crawler 호출
		double lat = Double.valueOf(req.getLatitude());
		double lng = Double.valueOf(req.getLongitude());
		log.info("lat = {}, lng = {}", lat, lng);
		
		log.info("Sun parameter Crawling Start!!!");
		startTime = System.currentTimeMillis();
		crawler.run(lat, lng, req.getDate()); // 현재는 임시로 x, y = 0 으로 둠, hillshade 알고리즘과 맞춰봐야됨
		endTime = System.currentTimeMillis();
		log.info("Sun parameter 크롤링에 걸린 시간 = {} sec 입니다.", (endTime - startTime) / 1000);
		
		SchedulerSunInfo si = new SchedulerSunInfo(lat, lng, crawler.getSi());
		
		// 모든 DSM 가져오는 코드 테스트
		startTime = System.currentTimeMillis();
		List<Dsm> dsms = dsmService.getDsm("11");
		endTime = System.currentTimeMillis();
		log.info("전체 DSM을 가져오는 데 걸린 시간 = {} sec 입니다.", (endTime - startTime) / 1000);
		
		// 전체 DSM을 분할하여 계산
		int cnt = 1;
		for(int i=0; i<dsms.size(); i+=3000000){
			System.out.println();
			log.warn("dsm 분할 {} 번 째!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", cnt++);
			startTime = System.currentTimeMillis();
			ArrayList<ArrayList<Dsm>> dsm2DArr = dsmService.dsm2DConverter(dsms.subList(i, Math.min(dsms.size(), i + 3000000)));
			endTime = System.currentTimeMillis();
			log.info("Dsm을 2D Arr로 변경하는데 걸리는 시간 = {} sec 입니다.", (endTime - startTime) / 1000);
			/*log.info("DSMS ARR 크기 = {}", dsms.size());
			dsms = null;
			System.gc();*/
			
			// 각 DSM 파일들 HillShade 계산
			log.info("start calc hillshade!!!");
			startTime = System.currentTimeMillis();
			int time = req.getTime().charAt(0) == '0' ? req.getTime().charAt(1) - '0' : Integer.valueOf(req.getTime());

			ArrayList<Hillshade> hs1DArr = hillShadeService.run(dsm2DArr, si.getArr().get(time));
			endTime = System.currentTimeMillis();
			log.info("hillShade 값을 계산하는데 걸린 시간 = {} sec 입니다.", (endTime - startTime) / 1000);
			
			// 계산된 HillShade값 DB에 갱신
			startTime = System.currentTimeMillis();
			roadService.calcRoadHillShade(hs1DArr);
			roadService.updateRoadHillShade();
			endTime = System.currentTimeMillis();
			log.info("계산된 hillShade 값을 DB에 갱신하는데 걸린 시간 = {} sec 입니다.", (endTime - startTime) / 1000);
		}
		
		allEndTime = System.currentTimeMillis();
		log.info("모든 코드를 수행하는데 걸린 시간 = {} sec 입니다.", (allEndTime - allStartTime) / 1000);
	}
}

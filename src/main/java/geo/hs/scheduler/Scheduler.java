package geo.hs.scheduler;

import geo.hs.crawling.Crawler;
import geo.hs.model.dsm.Dsm;
import geo.hs.model.scheduler.SchedulerSunInfo;
import geo.hs.repository.GetDsmRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Scheduler {
	
	private Crawler crawler = new Crawler();
	private ArrayList<SchedulerSunInfo> ssi = new ArrayList<>();
	private GetDsmRepository getDsmRepository = new GetDsmRepository();
	
	/**
	 * 태양 파라미터 크롤링
	 * 해당 함수가 가장 먼저 매일 0시에 호출됨
	 * 이유는 한 번만 전국적으로 크롤링 후 저장해놓고 hillshade 값을 계산하게 함
	 */
	@Scheduled(cron = "0 0 0 * * * " ) // 매일 0시에 호출
	public void sunCrawler(){
		LocalDate now = LocalDate.now();
		crawler.setDate(now.toString());
		
		// DSM 별로 (위,경도 각각 1도씩) crawling
		for(double lat = 30.0; lat <= 39.0; lat += 1.0){
			for(double lng = 125.0; lng <= 129.0; lng += 1.0){
				// 존재하는 위,경도만 뽑아내기
				if(lng == 125.0 && lat <= 33.0) continue;
				else if(lng == 126.0 && lat <= 32.0) continue;
				else if(lng == 127.0 && lat <= 33.0) continue;
				else if(lng == 128.0 && lat == 30.0) continue;
				else if(lng == 129.0 && lat >= 38.0) continue;
				
				// crawler 호출
				crawler.run(lat, lng, 0, 0); // 현재는 임시로 x, y = 0 으로 둠, hillshade 알고리즘과 맞춰봐야됨
				
				ssi.add(new SchedulerSunInfo(lat, lng, crawler.getSi()));
			}
		}
	}
	
	// @Scheduled(cron = "0 0 0 * * * " ) 매일 0시에 호출
	public void scheduler_128_37(){ // Thread로 만들기 위해 이렇게 이름 지었음
		// get dsm from db
		List<Dsm> dsm = getDsmRepository.getDsm(12837);
		
		// hillShade 값 계산
		
		// shp 파일과 비교 후 hillShade 값 계산
	}
}

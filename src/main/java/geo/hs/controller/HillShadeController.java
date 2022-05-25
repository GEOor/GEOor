package geo.hs.controller;

import geo.hs.model.DTO.PostHillShadeReq;
import geo.hs.model.dsm.Dsm;
import geo.hs.model.scheduler.SchedulerSunInfo;
import geo.hs.service.DsmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HillShadeController {
	
	private final DsmService dsmService;
	
	@Autowired
	public HillShadeController(DsmService dsmService) {
		this.dsmService = dsmService;
	}
	
	@PostMapping("/hillShade")
	void updateHillShade(@ModelAttribute PostHillShadeReq req){
		
		// 해당 cityCode에 맞는 지역의 DSM 가져오기
		List<Dsm> dsms = dsmService.getDsm(req.getCityId());
		dsmService.dsm2DConverter(dsms);
		
		// 태양고도각 크롤링
		// crawler 호출
		
		// 각 DSM 파일들 HillShade 계산
		
		
		// 계산된 HillShade값 DB에 갱신
		
	}
	
}

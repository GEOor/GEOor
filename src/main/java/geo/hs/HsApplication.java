package geo.hs;

import geo.hs.service.HillShadeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HsApplication {

	private static HillShadeService hsService = new HillShadeService();

	public static void main(String[] args) {

		//hillshade test
		System.out.println("--start--");
		//hsService.run();
		SpringApplication.run(HsApplication.class, args);
	}

}

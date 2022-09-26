package geo.hs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HsApplication.class, args);
	}

}

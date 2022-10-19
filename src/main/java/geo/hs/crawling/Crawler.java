package geo.hs.crawling;

import geo.hs.model.sun.SunInfo;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Crawler {

	private String url = "https://astro.kasi.re.kr/life/pageView/10";
	private String address = "crawl";
	private Double latitude, longitude;
	private double x_dir, y_dir;
	private ArrayList<SunInfo> si = new ArrayList<>();

	public void run(double lat, double lng, String date) {
		WebDriver driver = null;
		WebElement element = null;

		latitude = lat;
		longitude = lng;
		x_dir = lat;
		y_dir = lng;

		try {
			// 크롬 버전에 맞게 chromedriver 설치
			WebDriverManager.chromedriver().setup();

			// Chrome 드라이버 인스턴스 설정
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--headless");
			chromeOptions.addArguments("--no-sandbox");

			// TODO ChromeDriver를 설정할 때 오류가 발생하고 있습니다.
			driver = new ChromeDriver(chromeOptions);

			// URL로 접속 (이때 address는 중요하지 않다. 위,경도 좌표만 제대로 입력하면 고도각이 출력된다)
			driver.get(url + "?useElevation=1&lat=" + lat + "&lng=" + lng + "&elevation=0&output_range=1&date=" + date
					+ "&hour=&minute=&second=&address=" + address);

			// 대기 설정
			driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

			List<WebElement> tr = ((ChromeDriver) driver)
					.findElementsByXPath("//*[@id=\"sun-height-table\"]/table/tbody/tr");

			// 출력
			// for(int i = 0; i<tr.size(); i++) System.out.println(tr.get(i).getText());

			crawlerParsing(tr);

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * 2021-11-24
	 * 작성자 : 김태현
	 * 내용 : 태양 고도각 관련 정보 크롤링 결과 파싱
	 * 목적 : 크롤러 정보 get 함수로 깔끔하게 보내기 위함
	 */

	private void crawlerParsing(List<WebElement> arr) {
		for (int i = 0; i < arr.size(); i++) {
			List<String> strings = Arrays.asList(arr.get(i).getText().split(" "));
			SunInfo s = new SunInfo(0, 0, 0D, 0D, 0, 0D, 0D, 0D, 0D);

			s.setTime(i);
			s.setLatitude(latitude);
			s.setLongitude(longitude);
			s.setX(x_dir);
			s.setY(y_dir);

			for (int k = 1; k <= 12; k += 3) {
				double hour = Double.parseDouble(strings.get(k));
				double minute = Double.parseDouble(strings.get(k + 1));
				double second = Double.parseDouble(strings.get(k + 2));

				// 적경은 단위가 시분초임 -> 초단위로 바꾸기
				double t = hour * 3600 + minute * 60 + second;

				// 적경을 각도로 변환. 각도 = 시간 + (분/60) + (초/3600))
				double degree = hour + minute / 60 + second / 3600;

				if (k == 1)
					s.setAzimuth(degree);
				else if (k == 4)
					s.setAltitude(degree);
				else if (k == 7)
					s.setAscension(t);
				else
					s.setDeclination(degree);
			}

			si.add(s);
		}
	}

}

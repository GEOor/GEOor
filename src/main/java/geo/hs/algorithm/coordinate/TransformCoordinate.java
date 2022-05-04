package geo.hs.algorithm.coordinate;

import lombok.Getter;
import lombok.Setter;

import static java.lang.Math.*;

@Getter @Setter
public class TransformCoordinate {
	
	// 좌표변환 변수들
	private double M0; // 투영원점에 대한 자오선 호장
	private double k0 = 1; // 원점 축적 계수
	private double X; // TM X 좌표
	private double Y; // TM Y 좌표
	private double dX = 600000; // 원전 가산값(N)
	private double dY = 200000; // 원전 가산값(E)
	private double a = 6378137; // 장반경(m)
	private double f = 1/298.257222101; // 편평률
	private double b; // 단반경(a(1-f))
	private double e2; // 제1이심률
	private double er; // 제2이심률
	private double u1; // 매개변수
	private double M; // 매개변수
	private double e1; // 매개변수
	private double o1; // 매개변수
	private double r1; // 위도 o1에서의 자오선의 곡률 반경
	private double c1; // 매개변수
	private double t1; // 매개변수
	private double n1; // 위도 o1에서의 묘유선의 곡률 반경
	private double D; // 매개변수
	private double lam0 = 2.21656815; // 투영 원점 경도 (10.405초 반영안함) 0.2890
	private double latitude; // 위도
	private double longitude; // 경도
	
	public void transform(){
		b = a*(1-f);
		double a2 = pow(a, 2);
		double b2 = pow(b, 2);
		e2 = (a2 - b2) / a2;
		er = (a2 - b2) / b2;
		double o = 0.663225116;
		M0 = 4207498.019;
		M = M0 + ((X - dX)/k0);
		u1 = M/(a*(1 - (e2/4) - ((3 * pow(e2,2))/64) - ((5 * pow(e2,3))/256)));
		double sqe = sqrt(1 - e2);
		e1 = (1 - sqe) / (1 + sqe);
		o1 = u1 +
				((3*e1/2) - (27* pow(e1, 3)/32))* sin(2*u1) +
				((21* pow(e1,2)/16) - (55* pow(e1, 4)/32)) * sin(4*u1) +
				(151* pow(e1,3)/96) * sin(6*u1) +
				(1097* pow(e1,4)/512) * sin(8*u1);
		r1 = (a * (1 - e2)) / sqrt(pow(1 - (e2 * pow(sin(o1),2)), 3));
		c1 = er * pow(cos(o1),2);
		t1 = pow(tan(o1),2);
		n1 = a / (sqrt(1 - e2 * pow(sin(o1), 2)));
		D = (Y - dY) / (n1*k0);
		calc();
	}
	
	private void calc(){
		latitude = o1 - (n1*tan(o1)/r1) * (
				(pow(D,2)/2) - (pow(D,4)/24) * (5+3*t1+10*c1-4*pow(c1,2)-9*er) +
						(pow(D,6)/720) * (61 + 90*t1 + 298*c1 + 45*pow(t1,2) - 252*er - 3*pow(c1,2))
		);
		
		longitude = lam0 + (1/cos(o1)) * (
				D - (pow(D,3)/6) * (1 + 2*t1 + c1) +
						(pow(D,5)/120) * (5 - 2*c1 + 28*t1 - 3*pow(c1,2) + 8*er + 24*pow(t1,2))
		);
		
		latitude = latitude * 180 / PI;
		
		longitude = longitude * 180 / PI;
	}
	
}


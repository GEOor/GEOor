package geo.hs.controller;

import geo.hs.algorithm.coordinate.TransformCoordinate;
import geo.hs.crawling.Crawler;
import geo.hs.model.dsm.DsmXyz;
import geo.hs.model.sun.SunInfo;

import java.util.ArrayList;

public class SunController {

    private ArrayList<ArrayList<SunInfo>> sunInfoArr = new ArrayList<ArrayList<SunInfo>>();

    private final Crawler crawler = new Crawler();

    public ArrayList<ArrayList<SunInfo>> getSunInfo() {return sunInfoArr;}


    /**
     2021-11-25
     작성자 : 천수환
     내용 : 2차원 dem ArrayList를 k * k등분, k^2개의 사각형으로 나누기
     작동 원리
     1) 2차원 SunInfo ArrayList를 초기화
     2) 등분하는 위치 선정
     3) 변환된 위,경도를 크롤러를 이용해 고도각으로 변환
     4) 변환된 고도각을 똑같이 2차원 SunInfo ArrayList의 사각형에 위치한 곳으로 모두 채운다.
     */

    public void cutSquare(ArrayList<ArrayList<DsmXyz>> dem, int num, int time){

        sunInfoArr = new ArrayList<ArrayList<SunInfo>>();

        //dem 크기만큼 빈 배열 만들어두기
        for (ArrayList<DsmXyz> dsmXyzs : dem) {
            ArrayList<SunInfo> resize = new ArrayList<SunInfo>();
            for (int j = 0; j < dsmXyzs.size(); j++) {
                SunInfo resize_si = new SunInfo(0, 0, 0D, 0D, 0, 0D, 0D, 0D, 0D);
                resize.add(resize_si);
            }
            sunInfoArr.add(resize);
        }

        TransformCoordinate tf;

        //단위 분배. num : 자르고 싶은 개수
        int unit_x = (dem.size() / num), unit_y = (dem.get(0).size() / num); //num등분 단위
        int pre_x = 0, pre_y = 0; //이전 index

        for(int i=0; i<num; i++){
            for(int j=0; j<num; j++){

                //가운데 부분
                int index_x = (i * unit_x + (i + 1) * unit_x) / 2;
                int index_y = (j * unit_y + (j + 1) * unit_y) / 2;
                double dem_x = dem.get(index_x).get(index_y).getX();
                double dem_y = dem.get(index_x).get(index_y).getY();

                tf = new TransformCoordinate();
                tf.setX(dem_y); tf.setY(dem_x);
                tf.transform();

                crawler.run(tf.getLatitude(), tf.getLongitude(), dem_y, dem_x);
                ArrayList<SunInfo> si = crawler.get();

                index_x = dem.size() / (i + 1); index_y = dem.get(0).size() / (j + 1);
                if(i == num - 1) index_x = dem.size();
                if(j == num - 1) index_y = dem.get(0).size();


                for(int x=pre_x; x<index_x; x++){
                    for(int y=pre_y; y<index_y;y++){
                        tf.setX(dem.get(x).get(y).getY()); tf.setY(dem.get(x).get(y).getX());
                        tf.transform();
                        SunInfo tmp = new SunInfo(dem.get(x).get(y).getY(), dem.get(x).get(y).getX(),
                                tf.getLatitude(), tf.getLongitude(), time, si.get(time).getAzimuth(),
                                si.get(time).getAltitude(), si.get(time).getAscension(), si.get(time).getDeclination());
                        sunInfoArr.get(x).set(y, tmp);
                    }
                }

                pre_x = index_x; pre_y = index_y;
            }
        }

    }
}

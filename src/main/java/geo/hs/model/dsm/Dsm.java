package geo.hs.model.dsm;

import geo.hs.algorithm.coordinate.TransformCoordinate;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

@Getter @Setter
public class Dsm {

    private File file;
    private ArrayList<Double> arrX = new ArrayList<Double>();
    private ArrayList<Double> arrY = new ArrayList<Double>();
    private ArrayList<Double> arrZ = new ArrayList<Double>();
    private ArrayList<ArrayList<DsmXyz>> arr = new ArrayList<ArrayList<DsmXyz>>();
    private int row, col;
    private TransformCoordinate tf = new TransformCoordinate();

    public Dsm(String path) {
        // 파일 객체 생성
        this.setFile(new File(path));
        System.out.println("completed setFile");
        setArr();
        toDemInfo();
    }

    private void setArr(){
        row = 0;
        col = 0;
        try{
            //입력 스트림 생성
            FileReader file_reader = new FileReader(file);
            String temp = "";
            int cur = 0;
            int flag = 0;
            double prev_x = -1;
            double prev_y = -1;
            ArrayList<Double> arrX = new ArrayList<Double>();
            ArrayList<Double> arrY = new ArrayList<Double>();
            ArrayList<Double> arrZ = new ArrayList<Double>();
            while((cur = file_reader.read()) != -1){
                char c = (char)cur;
                if(('0' <= c && c <= '9') || c == '.') {
                    temp += c;
                }
                else{
                    if(temp.equals("")) {
                        continue;
                    }
                    double cmp = Double.parseDouble(temp);
                    if(flag == 0) { // x 좌표
                        arrX.add(cmp);
                        if(prev_x == -1) prev_x = cmp;
                        if(prev_x != cmp){
                            prev_x = cmp;
                            row++;
                        }
                    }
                    else if(flag == 1){ // y 좌표
                        arrY.add(cmp);
                        if(prev_y == -1) prev_y = cmp;
                        if(prev_y != cmp){
                            prev_y = cmp;
                            col++;
                            if(col > 10) {
                                setArrX(arrX);
                                setArrY(arrY);
                                setArrZ(arrZ);
                                file_reader.close();
                                return;
                            }
                            row = 0;

                        }
                        System.out.println("row: " + row + " col: " + col);
                    }
                    else { // z 좌표
                        arrZ.add(Double.parseDouble(temp));
                    }
                    flag++;
                    flag %= 3;
                    temp = "";



                }


            }
            col++;
            setArrX(arrX);
            setArrY(arrY);
            setArrZ(arrZ);
            file_reader.close();




        } catch (FileNotFoundException e) {
            e.getStackTrace();
        } catch(IOException e){
            e.getStackTrace();
        }
    }

    /**
     x축 = 동, 서 방향을 결정 => 열
     y축 = 북, 남 방향을 결정 => 행

     따라서
     열이 증가함 -> 2차원 평면 상 지도가 오른쪽으로 감 (x 좌표 증가)
     행이 증가함 -> 2차원 평면 상 지도가 아래로 내려감 (y 좌표 감소)
     **/
    private void toDemInfo(){
        setRow(row);
        setCol(col);
        System.out.println("row: " + row + " col: " + col);
        ArrayList<Double> arrX = getArrX();
        ArrayList<Double> arrY = getArrY();
        ArrayList<Double> arrZ = getArrZ();
        ArrayList<ArrayList<DsmXyz>> tempArr = new ArrayList<ArrayList<DsmXyz>>();
        for(int i=0; i<row; i++){
            ArrayList<DsmXyz> temp = new ArrayList<>();
            for(int j=0; j<col; j++){
                double x = arrX.get(col*i + j);
                double y = arrY.get(col*i + j);
                double z = arrZ.get(col*i + j);
                tf.setX(y); tf.setY(x); tf.transform();
                //System.out.println(x + " " + y + " " + tf.getLatitude() + " " + tf.getLongitude());
                temp.add(new DsmXyz(x, y, z, tf.getLatitude(), tf.getLongitude()));
            }
            tempArr.add(temp);
        }
        setArr(tempArr);

    }
}

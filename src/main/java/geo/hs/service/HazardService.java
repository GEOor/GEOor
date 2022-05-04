package geo.hs.service;

import geo.hs.model.hazard.Hazard;
import geo.hs.repository.HazardRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class HazardService {

    private final HazardRepository hazardRepository = new HazardRepository();
    private final ArrayList<Hazard> bridges = new ArrayList<>();
    private final ArrayList<Hazard> tunnels = new ArrayList<>();

    public void run() throws IOException, ParseException, SQLException {
        getBridge();
        getTunnel();
        hazardRepository.saveBridge(bridges);
        hazardRepository.saveTunnel(tunnels);
    }

    private void getBridge() throws IOException, ParseException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1613000/btiData/getBrdgList"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=mWqQaDoKuEgZ6uOuwh6YVaxSjmlymrAML5TELthV%2FpJi9FHf4fYDL5O4VnQWTf1ks3eWxySwiNQF%2FnC0Nh1kWg%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("responseType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*데이터형식(xml/json)*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("100000", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("hyear","UTF-8") + "=" + URLEncoder.encode("2020", "UTF-8")); /*조회할 기준연도*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        String jsonStr = sb.toString();

        JSONParser parser = new JSONParser();
        Object obj = parser.parse( jsonStr );
        JSONObject jsonObj = (JSONObject) obj;

        Object resp = jsonObj.get("response");
        JSONObject respObj = (JSONObject) resp;
        Object resp2 = respObj.get("body");
        JSONObject respObj2 = (JSONObject) resp2;
        Object resp3 = respObj2.get("items");


        for (Object o: (JSONArray) resp3){
            JSONObject jo = (JSONObject) o;
            Hazard hazard = new Hazard(Double.parseDouble(jo.get("sLatitude").toString()), Double.parseDouble(jo.get("sLongitude").toString()));
            bridges.add(hazard);
        }
    }

    private void getTunnel() throws IOException, ParseException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1613000/btiData/getTunlList"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=mWqQaDoKuEgZ6uOuwh6YVaxSjmlymrAML5TELthV%2FpJi9FHf4fYDL5O4VnQWTf1ks3eWxySwiNQF%2FnC0Nh1kWg%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("responseType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*데이터형식(xml/json)*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("100000", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("hyear","UTF-8") + "=" + URLEncoder.encode("2020", "UTF-8")); /*조회할 기준연도*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        String jsonStr = sb.toString();

        JSONParser parser = new JSONParser();
        Object obj = parser.parse( jsonStr );
        JSONObject jsonObj = (JSONObject) obj;

        Object resp = jsonObj.get("response");
        JSONObject respObj = (JSONObject) resp;
        Object resp2 = respObj.get("body");
        JSONObject respObj2 = (JSONObject) resp2;
        Object resp3 = respObj2.get("items");


        for (Object o: (JSONArray) resp3){
            JSONObject jo = (JSONObject) o;
            Hazard hazard = new Hazard(Double.parseDouble(jo.get("sLatitude").toString()), Double.parseDouble(jo.get("sLongitude").toString()));
            tunnels.add(hazard);
        }
    }
}
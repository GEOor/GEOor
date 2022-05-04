package geo.hs.service;

import geo.hs.model.hillshade.Hillshade;
import geo.hs.model.shp.Shp;
import geo.hs.repository.DsmRepository;
import geo.hs.repository.JdbcTemplate;
import geo.hs.repository.ShpSaveRepository;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static geo.hs.config.ApplicationProperties.getProperty;

public class ShpService {
    private final ArrayList<Shp> shps = new ArrayList<>();
    private final JdbcTemplate jdbcTemplate;
    private final ShpSaveRepository shpSaveRepository;
    private final DsmRepository dsmRepository;


    public ShpService() {
        jdbcTemplate = new JdbcTemplate(getProperty("shp.table"));
        shpSaveRepository = new ShpSaveRepository();
        dsmRepository = new DsmRepository();
        findShpFiles();
    }

    /**
     * application.properties 에 지정된
     * directory 내의 모든 shp 파일 탐색 후 이름 저장
     */
    public void findShpFiles() {
        File directory = new File(getProperty("shp.directory"));
        File[] files = directory.listFiles((dir, name)
                -> name.endsWith("shp"));
        for (File file : files) {
            shps.add(new Shp(file.getPath()));
        }
    }

    public void init() throws SQLException {
        // 시작 전 shp 파일들이 있는지 확인
        if (shps.isEmpty()) {
            System.out.println("shp 파일이 없습니다. 경로를 확인해주세요.");
            return;
        }
        // 시작 전 테이블 확인
        try(Connection conn = jdbcTemplate.getConnection()) {
            if(jdbcTemplate.tableNotExist(conn)) {
                jdbcTemplate.createTable(conn);
            }
        }
        // 테이블과 shp 파일 둘 다 확인됐으면 shp 정보 삽입
        try (Connection conn = jdbcTemplate.getConnection()) {
            // 테이블에서 column 목록 가져옴
            ArrayList<String> columns = jdbcTemplate.getColumns(conn);
            shpSaveRepository.save(conn, shps, columns);
            conn.commit();
        }
    }

    // hillshadeArr의 0번 element는 빈 값
    public void demMapping(ArrayList<Hillshade> hillshadeArr) throws SQLException {
        try (Connection conn = jdbcTemplate.getConnection()) {
            dsmRepository.findOverlapPolygon(conn, hillshadeArr);
            dsmRepository.updateHillShade(conn);
            conn.commit();
        }
    }
    /*
     * 교량, 터널을 더이상 hillshade 값으로 치환하지 않아도 돼서 필요없어진 부분입니다.
    public void applyHazard(ArrayList<Hazard> lake, ArrayList<Hazard> turnel) throws SQLException {
        HazardRepository hazardRepository = new HazardRepository();
        try (Connection conn = jdbcTemplate.getConnection()) {
            //hazardRepository.applyHazard(conn, lake, turnel);
            conn.commit();
        }
    }
    */

}

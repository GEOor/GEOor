package geo.hs.service;

import geo.hs.model.dsm.Dsm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DsmServiceTest {

    @Autowired
    private DsmService dsmService;

    @Test
    @DisplayName("강동구에 있는 Dsm 개수가 제대로 나오는지 확인")
    void getDsmTest() {
        // given

        // when
        List<Dsm> dsmList = dsmService.getDsm(11740);

        // then
        assertEquals(32438, dsmList.size());
    }

    @Test
    @DisplayName("강동구에 있는 첫번째 Dsm의 세부적인 정보 확인")
    void getDsmDetailTest() {
        // given
        List<Dsm> dsmList = dsmService.getDsm(11740);

        // when
        Dsm dsm = dsmList.get(0);

        // then
        assertEquals("127.144305555556", dsm.getX());
        assertEquals("37.5220833333333", dsm.getY());
        assertEquals(59.0, dsm.getZ());
        assertEquals(0, dsm.getHillShade());
    }

    @Test
    @DisplayName("강동구 dsm2DConverter 데이터 개수 확인")
    void dsm2DConverterTest() {
        // given
        List<Dsm> dsmList = dsmService.getDsm(11740);

        // when
        ArrayList<ArrayList<Dsm>> dsmArrayList = dsmService.dsm2DConverter(dsmList);

        // then
        assertEquals(231, dsmArrayList.size());
        assertEquals(268, dsmArrayList.get(0).size());
        assertEquals(268, dsmArrayList.get(1).size());

    }

    @Test
    @DisplayName("강동구 dsm2DConverter 데이터 세부 확인")
    void dsm2DConverterDetailTest() {
        // given
        List<Dsm> dsmList = dsmService.getDsm(11740);

        // when
        ArrayList<ArrayList<Dsm>> dsmArrayList = dsmService.dsm2DConverter(dsmList);

        // then
        assertEquals("1", dsmArrayList.get(0).get(0).getX());
        assertEquals("1", dsmArrayList.get(0).get(0).getY());
        assertEquals(-1, dsmArrayList.get(0).get(0).getZ());
        assertEquals(-1, dsmArrayList.get(0).get(0).getHillShade());
    }
}
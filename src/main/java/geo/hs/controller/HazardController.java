package geo.hs.controller;

import geo.hs.model.hazard.Hazard;
import geo.hs.repository.HazardRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("/hazard")
public class HazardController {

    private static final HazardRepository hazardRepository = new HazardRepository();

    /**
     * 교량 데이터를 가지고 옴
     */
    @GetMapping("/bridge")
    public ArrayList<Hazard> bridge(Model model) throws SQLException {
        return hazardRepository.getBridge();
    }

    /**
     * 터널 데이터를 가지고 옴
     */
    @GetMapping("/tunnel")
    public ArrayList<Hazard> tunnel(Model model) throws SQLException {
        return hazardRepository.getTunnel();
    }

    /**
     * 결빙 데이터를 가지고 옴
     */
    @GetMapping("/frozen")
    public ArrayList<Hazard> frozen(Model model) throws SQLException {
        return hazardRepository.getFrozen();
    }
}
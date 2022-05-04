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

    @GetMapping("/bridge")
    public ArrayList<Hazard> bridge(Model model) throws SQLException {
        return hazardRepository.getBridge();
    }

    @GetMapping("/tunnel")
    public ArrayList<Hazard> tunnel(Model model) throws SQLException {
        return hazardRepository.getTunnel();
    }
}
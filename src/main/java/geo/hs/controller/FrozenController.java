package geo.hs.controller;

import geo.hs.model.Frozen.Frozen;
import geo.hs.repository.FrozenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FrozenController {

	private final FrozenRepository frozenRepository;
	
	@Autowired
	public FrozenController(FrozenRepository frozenRepository) {
		this.frozenRepository = frozenRepository;
	}
	
	@GetMapping("/frozen")
	public List<Frozen> frozen(){
		return frozenRepository.getFrozenRoad();
	}
}

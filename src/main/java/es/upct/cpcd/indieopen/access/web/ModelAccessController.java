package es.upct.cpcd.indieopen.access.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.unit.UnitService;

@RestController
@RequestMapping("/model")
public class ModelAccessController {

	private final UnitService unitsService;

	@Autowired
	public ModelAccessController(UnitService unitsService) {
		this.unitsService = unitsService;
	}

	@GetMapping(produces = MediaType.TEXT_PLAIN_VALUE, value = "/{resource}")
	public String getModelByUnitResourceId(@PathVariable String resource) throws INDIeException {
		return unitsService.getModelByUnitResourceId(resource);
	}

}

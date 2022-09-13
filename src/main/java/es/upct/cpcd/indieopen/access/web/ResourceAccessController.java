package es.upct.cpcd.indieopen.access.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.course.CourseService;
import es.upct.cpcd.indieopen.unit.UnitService;

@RestController
@RequestMapping("/access")
public class ResourceAccessController {

	private final UnitService unitsService;
	private final CourseService courseService;

	@Autowired
	public ResourceAccessController(UnitService unitsService, CourseService courseService) {
		this.unitsService = unitsService;
		this.courseService = courseService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{resource}/{type}")
	public boolean getAuthorizationForUnit(@PathVariable String resource, @PathVariable String type,
			@RequestParam(name = "email") String email) throws INDIeException {

		if ("unit".equals(type)) {
			return unitsService.getAuthorizationForUnit(resource, email);
		} else if ("course".equals(type)) {
			return courseService.getAuthorizationForCourse(resource, email);
		}

		return false;

	}

}

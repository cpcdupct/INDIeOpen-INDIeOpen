package es.upct.cpcd.indieopen.course.web;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.course.CourseService;
import es.upct.cpcd.indieopen.course.dto.CourseWithContent;

@RestController
@RequestMapping("/course/embed")
public class CourseEmbedController {

	private CourseService service;

	@Autowired
	public CourseEmbedController(CourseService service) {
		this.service = service;
	}

	@GetMapping(value = "/{externalId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> findCourseByExternalId(@PathVariable(name = "externalId") String externalId)
			throws INDIeException {
		CourseWithContent courseWithContent = service.findPublishedCourseByExternalId(externalId);
		JSONObject publishedData = courseWithContent.getJSONObjectFromDocument().getJSONObject("published");
		return ResponseEntity.status(HttpStatus.OK).body(publishedData.toString());
	}
}

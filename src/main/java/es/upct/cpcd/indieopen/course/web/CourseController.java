package es.upct.cpcd.indieopen.course.web;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.upct.cpcd.indieopen.common.BaseController;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.resources.LTIUser;
import es.upct.cpcd.indieopen.course.CourseService;
import es.upct.cpcd.indieopen.course.bean.CourseBean;
import es.upct.cpcd.indieopen.course.domain.Course;
import es.upct.cpcd.indieopen.unit.web.resources.TokenResource;

@RestController
@RequestMapping("/courses")
public class CourseController extends BaseController {

	private final CourseService courseService;

	@Autowired
	public CourseController(CourseService courseService) {
		this.courseService = courseService;
	}

	@GetMapping()
	public Page<CourseResource> findCoursesByUser(
			@PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable page) {
		return courseService.findCoursesByUser(getCurrentUserId(), page);
	}

	@PostMapping()
	public ResponseEntity<?> createCourse(@RequestBody CourseBean courseBean) throws INDIeException {
		Course course = courseService.createCourse(getCurrentUserId(), courseBean);
		return ResponseEntity.created(URI.create("/api/courses/" + course.getId())).build();
	}

	@GetMapping(value = "/{courseId}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
	public TokenResource generateEditToken(@PathVariable int courseId) throws INDIeException {
		String token = courseService.generateToken(getCurrentUserId(), courseId);
		return TokenResource.from(token);
	}

	@DeleteMapping(value = "/{courseId}")
	public ResponseEntity<?> deleteCourse(@PathVariable int courseId) throws INDIeException {
		courseService.deleteCourse(getCurrentUserId(), courseId);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/{courseId}/publish")
	public ResponseEntity<?> publishCourse(@PathVariable int courseId) throws INDIeException {
		courseService.publishCourse(getCurrentUserId(), courseId);
		return ResponseEntity.ok().build();
	}

	// Access
	@GetMapping(value = "/{courseId}/access")
	public List<LTIUser> getAccessesForCourse(@PathVariable int courseId) throws INDIeException {
		return courseService.getAccessesForCourse(getCurrentUserId(), courseId);
	}

	@PutMapping(value = "/{courseId}/access")
	public ResponseEntity<?> modifiyAccessesForCourse(@PathVariable int courseId, @RequestBody List<String> emails)
			throws INDIeException {
		this.courseService.putUnitAccesesForCourse(getCurrentUserId(), courseId, emails);
		return ResponseEntity.ok().build();
	}

}

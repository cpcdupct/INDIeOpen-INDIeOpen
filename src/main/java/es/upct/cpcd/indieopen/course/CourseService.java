package es.upct.cpcd.indieopen.course;

import java.util.List;

import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.resources.LTIUser;
import es.upct.cpcd.indieopen.course.bean.CourseBean;
import es.upct.cpcd.indieopen.course.domain.Course;
import es.upct.cpcd.indieopen.course.domain.CourseRepository;
import es.upct.cpcd.indieopen.course.dto.CourseWithContent;
import es.upct.cpcd.indieopen.course.web.CourseResource;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.token.TokenParser;
import es.upct.cpcd.indieopen.user.domain.UserRepository;

@Service
@Validated
@Transactional(rollbackFor = INDIeException.class)
public class CourseService {
	private final CourseFinder courseFinder;
	private final CourseCreator courseCreator;
	private final CoursePublisher coursePublisher;
	private final CourseAccessManager courseAccessManager;

	@Autowired
	public CourseService(CourseRepository courseRepository, UserRepository userRepository,
			DocumentDBManager documentManager, TokenParser tokenParser) {
		this.courseFinder = new CourseFinder(courseRepository, documentManager, tokenParser);
		this.courseCreator = new CourseCreator(courseRepository, userRepository, documentManager, courseFinder);
		this.coursePublisher = new CoursePublisher(documentManager, courseFinder);
		this.courseAccessManager = new CourseAccessManager(courseFinder, userRepository, courseRepository);
	}

	public CourseWithContent findCourseWithContentById(String userId, int courseId) throws INDIeException {
		return this.courseFinder.findCourseWithContentById(userId, courseId);
	}

	public Page<CourseResource> findCoursesByUser(String userId, Pageable page) {
		return this.courseFinder.findCoursesByUser(userId, page).map(CourseResource::fromCourse);
	}

	public Course createCourse(String userId, @Valid CourseBean courseBean) throws INDIeException {
		return this.courseCreator.createCourse(userId, courseBean);
	}

	public void saveCourseData(String user, int entity, JSONObject model) throws INDIeException {
		this.courseCreator.saveCourseEditorData(user, entity, model);
	}

	public String generateToken(String userId, int courseId) throws INDIeException {
		return this.courseFinder.generateToken(userId, courseId);
	}

	public void publishCourse(String userId, int courseId) throws INDIeException {
		this.coursePublisher.publishCourse(userId, courseId);
	}

	public void deleteCourse(String userId, int courseId) throws INDIeException {
		this.courseCreator.deleteCourse(userId, courseId);
	}

	public CourseWithContent findPublishedCourseByExternalId(String externalId) throws INDIeException {
		return this.courseFinder.findCourseWithContentByExternalId(externalId);
	}

	public List<LTIUser> getAccessesForCourse(String userId, int courseId) throws INDIeException {
		return this.courseAccessManager.getAccessesForCourse(userId, courseId);
	}

	public void putUnitAccesesForCourse(String userId, int courseId, List<String> emails) throws INDIeException {
		this.courseAccessManager.putUnitAccesesForCourse(userId, courseId, emails);
	}

	public boolean getAuthorizationForCourse(String courseId, String email) throws INDIeException {
		return this.courseAccessManager.getAuthorizationForCourse(courseId, email);
	}
}

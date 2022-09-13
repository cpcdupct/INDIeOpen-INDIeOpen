package es.upct.cpcd.indieopen.course;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import es.upct.cpcd.indieopen.common.exceptions.ErrorField;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.common.resources.LTIUser;
import es.upct.cpcd.indieopen.course.domain.Course;
import es.upct.cpcd.indieopen.course.domain.CourseAccess;
import es.upct.cpcd.indieopen.course.domain.CourseRepository;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;
import es.upct.cpcd.indieopen.utils.StringUtils;

public class CourseAccessManager {

	private final CourseFinder courseFinder;
	private final UserRepository userRepository;
	private final CourseRepository courseRepository;

	CourseAccessManager(CourseFinder courseFinder, UserRepository userRepository, CourseRepository courseRepository) {
		this.courseFinder = courseFinder;
		this.userRepository = userRepository;
		this.courseRepository = courseRepository;
	}

	List<LTIUser> getAccessesForCourse(String userId, int courseId) throws INDIeException {
		Course course = courseFinder.findCourseByUserAndId(userId, courseId);

		List<LTIUser> listOfUsers = new ArrayList<>();

		for (CourseAccess access : course.getAuthorizedAccesses()) {
			Optional<UserData> userQuery = this.userRepository.findByEmail(access.getEmail());
			listOfUsers.add(LTIUser.fromCourseAccess(access, userQuery.isPresent() ? userQuery.get() : null));
		}

		return listOfUsers;
	}

	void putUnitAccesesForCourse(String userId, int courseId, List<String> emails) throws INDIeException {
		if (!isEmailsValid(emails))
			throw INDIeExceptionFactory.createWrongParamsException(ErrorField.listOf("emails", "Invalid emails"));

		Course course = courseFinder.findCourseByUserAndId(userId, courseId);

		course.getAuthorizedAccesses().clear();
		courseRepository.save(course);

		for (String email : emails)
			course.getAuthorizedAccesses().add(new CourseAccess(course, email));

		courseRepository.save(course);
	}

	private boolean isEmailsValid(List<String> emails) {
		if (emails == null)
			return false;
		return !(emails.stream().anyMatch(email -> !StringUtils.isEmailValid(email)));
	}

	public boolean getAuthorizationForCourse(String courseId, String email) throws INDIeException {
		Course course = courseRepository.findByExternalID(courseId)
				.orElseThrow(() -> new INDIeExceptionBuilder("Unit access not authorized").status(Status.UNAUTHORIZED)
						.errorFields(ErrorField.listOf(courseId, "Not authorized")).build());

		if (course.getAuthorizedAccesses().stream().anyMatch(access -> access.getEmail().equals(email))
				|| course.getAuthor().getEmail().equals(email))
			return true;
		else
			throw new INDIeExceptionBuilder("Course access not authorized").status(Status.UNAUTHORIZED)
					.errorFields(ErrorField.listOf(courseId, "Not authorized")).build();
	}
}

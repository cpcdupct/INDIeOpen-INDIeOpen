package es.upct.cpcd.indieopen.course;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.upct.cpcd.indieopen.common.exceptions.ErrorCodes;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.course.domain.Course;
import es.upct.cpcd.indieopen.course.domain.CourseRepository;
import es.upct.cpcd.indieopen.course.dto.CourseWithContent;
import es.upct.cpcd.indieopen.services.document.DocumentDBCollection;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.token.ContentType;
import es.upct.cpcd.indieopen.token.TokenParser;

class CourseFinder {
	private final CourseRepository courseRepository;
	private final DocumentDBManager documentDbManager;
	private final TokenParser tokenParser;

	CourseFinder(CourseRepository courseRepository, DocumentDBManager documentDbManager, TokenParser tokenParser) {
		this.courseRepository = courseRepository;
		this.tokenParser = tokenParser;
		this.documentDbManager = documentDbManager;
	}

	CourseWithContent findCourseWithContentById(String userId, int courseId) throws INDIeException {
		Course course = courseRepository.findByIdAndAuthorId(courseId, userId)
				.orElseThrow(() -> new INDIeExceptionBuilder("Course not found").status(Status.USER_ERROR)
						.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());

		Document document = documentDbManager.findDocument(DocumentDBCollection.COURSES, course.getDocumentID())
				.orElseThrow(() -> new INDIeExceptionBuilder("Course document not found").status(Status.INTERNAL_ERROR)
						.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());

		return new CourseWithContent(course, document);
	}

	Page<Course> findCoursesByUser(String userId, Pageable page) {
		return courseRepository.findByAuthorId(userId, page);
	}

	String generateToken(String userId, int courseId) throws INDIeException {
		Course course = courseRepository.findByIdAndAuthorId(courseId, userId)
				.orElseThrow(() -> new INDIeExceptionBuilder("Course not found").status(Status.USER_ERROR)
						.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());

		return this.tokenParser.generateToken(course.getId(), userId, ContentType.COURSE);
	}

	Course findCourseByUserAndId(String userId, int courseId) throws INDIeException {
		return courseRepository.findByIdAndAuthorId(courseId, userId)
				.orElseThrow(() -> new INDIeExceptionBuilder("Course not found").status(Status.USER_ERROR)
						.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());
	}

	CourseWithContent findCourseWithContentByExternalId(String externalId) throws INDIeException {
		Course course = courseRepository.findByExternalID(externalId)
				.orElseThrow(() -> new INDIeExceptionBuilder("Course not found").status(Status.USER_ERROR)
						.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());

		if (!course.isPublished())
			throw new INDIeExceptionBuilder("Course not published").status(Status.USER_ERROR)
					.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build();

		Document document = documentDbManager.findDocument(DocumentDBCollection.COURSES, course.getDocumentID())
				.orElseThrow(() -> new INDIeExceptionBuilder("Course document not found").status(Status.INTERNAL_ERROR)
						.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());

		return new CourseWithContent(course, document);
	}

}

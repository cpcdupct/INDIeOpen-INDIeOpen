package es.upct.cpcd.indieopen.course;

import javax.validation.Valid;

import org.bson.Document;
import org.json.JSONObject;

import es.upct.cpcd.indieopen.common.exceptions.ErrorCodes;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.course.bean.CourseBean;
import es.upct.cpcd.indieopen.course.domain.Course;
import es.upct.cpcd.indieopen.course.domain.CourseBuilder;
import es.upct.cpcd.indieopen.course.domain.CourseRepository;
import es.upct.cpcd.indieopen.course.dto.CourseWithContent;
import es.upct.cpcd.indieopen.services.document.DocumentDBCollection;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.services.document.DocumentDataException;
import es.upct.cpcd.indieopen.services.document.DocumentHelper;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;
import es.upct.cpcd.indieopen.utils.ObjectUtils;

class CourseCreator {
	private final CourseRepository courseRepository;
	private final UserRepository userRepository;
	private final DocumentDBManager documentDbManager;
	private final CourseFinder courseFinder;

	CourseCreator(CourseRepository courseRepository, UserRepository userRepository, DocumentDBManager documentDbManager,
			CourseFinder courseFinder) {
		this.courseRepository = courseRepository;
		this.documentDbManager = documentDbManager;
		this.userRepository = userRepository;
		this.courseFinder = courseFinder;
	}

	Course createCourse(String userId, @Valid CourseBean courseBean) throws INDIeException {
		UserData author = userRepository.findById(userId)
				.orElseThrow(() -> new INDIeExceptionBuilder("User not found  ").status(Status.USER_ERROR)
						.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());

		String documentId;

		try {
			// Create the document
			documentId = documentDbManager.storeDocument(DocumentDBCollection.COURSES,
					DocumentHelper.createEmptyCourseDocument());

			// Create the course
			Course course = CourseBuilder.createBuilder().withName(courseBean.getName()).withAuthor(author)
					.withDocumentId(documentId).build();

			//
			course = courseRepository.save(course);

			return course;
		} catch (DocumentDataException ex) {
			throw new INDIeExceptionBuilder("Error in create course", ex).status(Status.INTERNAL_ERROR).build();
		}
	}

	void saveCourseEditorData(String user, int entity, JSONObject editorData) throws INDIeException {
		ObjectUtils.requireNonNull(editorData);

		CourseWithContent courseWithContent = courseFinder.findCourseWithContentById(user, entity);
		Course course = courseWithContent.getCourse();
		Document document = courseWithContent.getDocument();
		course.setDraft(true);

		try {
			DocumentHelper.updateCourseEditorDataDocument(document, editorData);
			documentDbManager.replaceDocument(DocumentDBCollection.COURSES, course.getDocumentID(), document);
		} catch (DocumentDataException e) {
			throw new INDIeExceptionBuilder("Error in saveCourseData", e).build();
		}

	}

	public void deleteCourse(String userId, int courseId) throws INDIeException {
		Course course = courseRepository.findByIdAndAuthorId(courseId, userId)
				.orElseThrow(() -> new INDIeExceptionBuilder("Course not found").status(Status.USER_ERROR)
						.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());

		try {
			documentDbManager.deleteDocument(DocumentDBCollection.COURSES, course.getDocumentID());
			courseRepository.delete(course);

		} catch (DocumentDataException e) {
			throw new INDIeExceptionBuilder("Error in deleteCourse", e).build();
		}
	}
}

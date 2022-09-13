package es.upct.cpcd.indieopen.course;

import org.bson.Document;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.course.domain.Course;
import es.upct.cpcd.indieopen.course.dto.CourseWithContent;
import es.upct.cpcd.indieopen.services.document.DocumentDBCollection;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.services.document.DocumentDataException;

class CoursePublisher {

	private DocumentDBManager documentManager;
	private CourseFinder courseFinder;

	CoursePublisher(DocumentDBManager documentManager, CourseFinder courseFinder) {
		this.documentManager = documentManager;
		this.courseFinder = courseFinder;
	}

	void publishCourse(String userId, int courseId) throws INDIeException {
		CourseWithContent courseAndContent = this.courseFinder.findCourseWithContentById(userId, courseId);
		Course course = courseAndContent.getCourse();
		Document document = courseAndContent.getDocument();
		document.put("published", document.get("editor"));

		try {
			documentManager.replaceDocument(DocumentDBCollection.COURSES, course.getDocumentID(), document);
			course.publishCourse();
		} catch (DocumentDataException e) {
			throw new INDIeExceptionBuilder("Error in publishCourse", e).status(Status.INTERNAL_ERROR).build();
		}
	}
}

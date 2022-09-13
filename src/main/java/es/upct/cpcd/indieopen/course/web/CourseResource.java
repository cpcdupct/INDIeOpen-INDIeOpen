package es.upct.cpcd.indieopen.course.web;

import es.upct.cpcd.indieopen.course.domain.Course;
import es.upct.cpcd.indieopen.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CourseResource {

	private int id;
	private String name;
	private String createdAt;
	private String documentID;
	private String externalID;
	private boolean draft;
	private String publishedAt;

	public static CourseResource fromCourse(Course course) {
		CourseResource resource = new CourseResource();

		resource.id = course.getId();
		resource.name = course.getName();
		resource.createdAt = DateUtils.dateToISOString(course.getCreatedAt());
		resource.documentID = course.getDocumentID();
		resource.externalID = course.getExternalID();
		resource.draft = course.isDraft();
		resource.publishedAt = DateUtils.dateToISOString(course.getPublishedAt());

		return resource;
	}
}

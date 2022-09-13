package es.upct.cpcd.indieopen.course.dto;

import org.bson.Document;
import org.json.JSONObject;

import es.upct.cpcd.indieopen.course.domain.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseWithContent {
	private Course course;
	private Document document;

	public JSONObject getJSONObjectFromDocument() {
		return new JSONObject(document.toJson());
	}
}

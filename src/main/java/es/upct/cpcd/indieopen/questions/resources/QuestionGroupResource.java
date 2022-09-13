package es.upct.cpcd.indieopen.questions.resources;

import es.upct.cpcd.indieopen.questions.domain.QuestionGroup;
import lombok.Getter;

@Getter
public class QuestionGroupResource {

	private String key;
	private String name;

	public static QuestionGroupResource from(QuestionGroup group) {
		QuestionGroupResource resource = new QuestionGroupResource();
		resource.key = group.getGroupKey();
		resource.name = group.getGroupName();
		return resource;
	}
}

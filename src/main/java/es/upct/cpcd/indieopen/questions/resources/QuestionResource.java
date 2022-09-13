package es.upct.cpcd.indieopen.questions.resources;

import java.util.List;
import java.util.stream.Collectors;

import es.upct.cpcd.indieopen.questions.domain.Question;
import es.upct.cpcd.indieopen.questions.domain.TrueFalseQuestion;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class QuestionResource {

	private String id;
	private String type;
	private String text;
	private List<AnswerResource> answers;
	private boolean correct;
	private String[] tags;

	private QuestionGroupResource group;

	public static QuestionResource fromQuestion(Question question) {
		QuestionResource resource = new QuestionResource();

		resource.id = question.getID();
		resource.text = question.getQuestionText();

		resource.answers = question.getAnswers().stream().map(AnswerResource::fromAnswer).collect(Collectors.toList());
		resource.tags = question.getTagsArray();
		resource.type = question.getType();

		if (question.getGroup() != null)
			resource.group = QuestionGroupResource.from(question.getGroup());

		if (question instanceof TrueFalseQuestion)
			resource.correct = ((TrueFalseQuestion) question).getCorrectAnswer();

		return resource;
	}
}

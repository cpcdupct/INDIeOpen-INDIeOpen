package es.upct.cpcd.indieopen.questions.resources;

import es.upct.cpcd.indieopen.questions.domain.Answer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AnswerResource {

	private String text;
	private boolean correct;

	public static AnswerResource fromAnswer(Answer answer) {
		AnswerResource resource = new AnswerResource();

		resource.text = answer.getText();
		resource.correct = answer.isCorrect();

		return resource;
	}
}

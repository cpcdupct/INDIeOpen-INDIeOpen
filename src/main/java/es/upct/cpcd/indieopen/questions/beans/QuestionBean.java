package es.upct.cpcd.indieopen.questions.beans;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotBlank;

import es.upct.cpcd.indieopen.utils.validators.ValidateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QuestionBean {

	@ValidateEnum(value = QuestionType.class, enumMethod = "getValue", message = "Not valid")
	private String type;
	@NotBlank
	private String text;
	private AnswerBean[] answers;
	private boolean correct;
	private String[] tags;
	private String group;

	public QuestionType getQuestionType() {
		return QuestionType.get(type);
	}

	public boolean isAnyAnswerCorrect() {
		if (getQuestionType() == QuestionType.TRUE_FALSE)
			return true;

		return (getAnswersList().stream().anyMatch(AnswerBean::isCorrect));
	}

	public List<AnswerBean> getAnswersList() {
		if (answers == null || (answers.length == 0))
			return Collections.emptyList();

		return Arrays.asList(answers);
	}
}

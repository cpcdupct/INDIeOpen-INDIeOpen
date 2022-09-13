package es.upct.cpcd.indieopen.questions.domain;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import es.upct.cpcd.indieopen.user.domain.UserData;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@NoArgsConstructor
@DiscriminatorValue("SA")
public class SingleAnswerQuestion extends Question {
	private static final long serialVersionUID = 1L;

	private SingleAnswerQuestion(String text, UserData author, List<Answer> answers) {
		super(text, author);
		this.answers = answers;
	}

	static SingleAnswerQuestion create(String text, UserData author, List<Answer> answers) {
		if (answers.size() < 2)
			throw new IllegalArgumentException("There must be at least 2 answers");

		if (!isAnyAnswerCorrect(answers) || moreThanOneCorrectAnswer(answers))
			throw new IllegalArgumentException("there must be only one correct");

		return new SingleAnswerQuestion(text, author, answers);
	}

	public Answer getCorrectAnswer() {
		return this.answers.stream().filter(Answer::isCorrect).findFirst().orElseThrow(IllegalStateException::new);
	}

	@Override
	public Question getCopyOfQuestion() {
		return new SingleAnswerQuestion(this.getQuestionText(), this.getAuthor(),
				Answer.getCopiesOf(this.getAnswers()));
	}

	@Override
	public String getType() {
		return "SingleAnswer";
	}
}
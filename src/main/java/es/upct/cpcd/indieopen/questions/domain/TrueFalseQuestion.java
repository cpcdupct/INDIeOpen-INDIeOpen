package es.upct.cpcd.indieopen.questions.domain;

import java.util.Objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import es.upct.cpcd.indieopen.user.domain.UserData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("TF")
public class TrueFalseQuestion extends Question {
	private static final long serialVersionUID = 1L;
	private boolean correctAnswer;

	TrueFalseQuestion(String text, UserData author, boolean answer) {
		super(text, author);
		this.correctAnswer = answer;
	}

	public boolean getCorrectAnswer() {
		return correctAnswer;
	}

	public static TrueFalseQuestion create(String text, UserData author, boolean answer) {
		return new TrueFalseQuestion(text, author, answer);
	}

	@Override
	public Question getCopyOfQuestion() {
		return new TrueFalseQuestion(this.getQuestionText(), this.getAuthor(), this.correctAnswer);
	}

	@Override
	public String getType() {
		return "TrueFalse";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof TrueFalseQuestion)) {
			return false;
		}
		TrueFalseQuestion trueFalseQuestion = (TrueFalseQuestion) o;
		return correctAnswer == trueFalseQuestion.correctAnswer;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(correctAnswer);
	}

}
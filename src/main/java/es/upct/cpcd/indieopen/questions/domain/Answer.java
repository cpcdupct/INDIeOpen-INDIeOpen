package es.upct.cpcd.indieopen.questions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(exclude = "question")
@NoArgsConstructor
public class Answer implements Serializable {
	private static final long serialVersionUID = "MY_SERIAL_VERSION";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 300, columnDefinition = "TEXT")
	private String text;

	@Column(nullable = false)
	private boolean correct;

	@ManyToOne
	@JoinColumn(name = "question_id")
	private Question question;

	public Answer(String text, boolean correct) {
		this.text = text;
		this.correct = correct;
	}

	public static List<Answer> getCopiesOf(List<Answer> answers) {
		List<Answer> newAnswers = new ArrayList<>();

		for (Answer answer : answers) {
			newAnswers.add(new Answer(answer.getText(), answer.isCorrect()));
		}

		return newAnswers;
	}

}
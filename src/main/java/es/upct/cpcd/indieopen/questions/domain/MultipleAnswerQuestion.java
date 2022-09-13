package es.upct.cpcd.indieopen.questions.domain;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import es.upct.cpcd.indieopen.user.domain.UserData;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@NoArgsConstructor
@DiscriminatorValue("MA")
public class MultipleAnswerQuestion extends Question {
    private static final long serialVersionUID = 1L;

    private MultipleAnswerQuestion(String text, UserData author, List<Answer> answers) {
        super(text, author, answers);
    }

    public static MultipleAnswerQuestion create(String text, UserData author, List<Answer> answers) {
        if (answers.size() < 2)
            throw new IllegalArgumentException("There must be at least 2 answers");

        if (!isAnyAnswerCorrect(answers))
            throw new IllegalArgumentException("there must be one correct");

        return new MultipleAnswerQuestion(text, author, answers);
    }

    public List<Answer> getCorrectAnswers() {
        return this.answers.stream().filter(Answer::isCorrect).collect(Collectors.toList());
    }

    @Override
    public Question getCopyOfQuestion() {
        return new MultipleAnswerQuestion(this.getQuestionText(), this.getAuthor(),
                Answer.getCopiesOf(this.getAnswers()));
    }

    @Override
    public String getType() {
        return "MultipleAnswer";
    }

}

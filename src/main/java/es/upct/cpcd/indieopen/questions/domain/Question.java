package es.upct.cpcd.indieopen.questions.domain;

import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.utils.ModelUtils;
import es.upct.cpcd.indieopen.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "QUESTION_TYPE")
@Entity
public abstract class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 32)
    private String ID;

    @Column(nullable = false, length = 300, columnDefinition = "TEXT")
    private String questionText;

    @ManyToOne(optional = false)
    @JoinColumn(name = "AUTHOR_ID")
    private UserData author;

    @Embedded
    private QuestionGroup group;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderColumn
    protected List<Answer> answers;

    @Column(columnDefinition = "TEXT", length = 700)
    protected String rawTags;

    public Question() {
        this.ID = ModelUtils.randomUUID(true);
    }

    protected Question(String text, UserData author) {
        this.ID = ModelUtils.randomUUID(true);
        this.questionText = text;
        this.author = author;
    }

    protected Question(String text, UserData author, List<Answer> answers) {
        this.ID = ModelUtils.randomUUID(true);
        this.questionText = text;
        this.author = author;
        this.answers = answers;
    }

    public void setRawTagsFromTagsArray(String... tags) {
        if (tags == null)
            throw new IllegalArgumentException("tags cannot be null or empty");

        this.rawTags = (tags.length > 0) ? String.join(";", tags) : null;
    }

    public String[] getTagsArray() {
        if (!StringUtils.isStringValid(rawTags))
            return new String[0];

        return rawTags.split(";");
    }

    public static boolean isAnyAnswerCorrect(List<Answer> answers) {
        return (answers.stream().anyMatch(Answer::isCorrect));
    }

    public static boolean moreThanOneCorrectAnswer(List<Answer> answers) {
        return (answers.stream().filter(Answer::isCorrect).count() > 1);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Question)) {
            return false;
        }
        Question question = (Question) o;
        return Objects.equals(ID, question.ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    public abstract Question getCopyOfQuestion();

    public abstract String getType();


    public List<Answer> getAnswers() {
        return Objects.requireNonNullElse(answers, Collections.emptyList());
    }

    public void addAnswers(List<Answer> answers) {
        this.answers.addAll(answers);
    }

    public void clearAnswers() {
        for (Answer answer : answers) {
            answer.setQuestion(null);
        }

        answers.clear();
    }
}
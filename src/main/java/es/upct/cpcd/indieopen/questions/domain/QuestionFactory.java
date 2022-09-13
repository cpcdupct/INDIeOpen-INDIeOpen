package es.upct.cpcd.indieopen.questions.domain;

import java.util.List;

import es.upct.cpcd.indieopen.user.domain.UserData;

public class QuestionFactory {

    public TrueFalseQuestion createTrueFalseQuestion(String text, UserData author, boolean correct) {
        return TrueFalseQuestion.create(text, author, correct);
    }

    public SingleAnswerQuestion createSingleAnswerQuestion(String text, UserData author, List<Answer> answers) {
        return SingleAnswerQuestion.create(text, author, answers);
    }

    public MultipleAnswerQuestion createMultipleAnswerQuestion(String text, UserData author, List<Answer> answers) {
        return MultipleAnswerQuestion.create(text, author, answers);
    }

}
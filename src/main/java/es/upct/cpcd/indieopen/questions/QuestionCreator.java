package es.upct.cpcd.indieopen.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import es.upct.cpcd.indieopen.common.exceptions.ErrorCodes;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.questions.beans.AnswerBean;
import es.upct.cpcd.indieopen.questions.beans.QuestionBean;
import es.upct.cpcd.indieopen.questions.beans.QuestionType;
import es.upct.cpcd.indieopen.questions.domain.Answer;
import es.upct.cpcd.indieopen.questions.domain.MultipleAnswerQuestion;
import es.upct.cpcd.indieopen.questions.domain.Question;
import es.upct.cpcd.indieopen.questions.domain.QuestionFactory;
import es.upct.cpcd.indieopen.questions.domain.QuestionGroup;
import es.upct.cpcd.indieopen.questions.domain.QuestionRepository;
import es.upct.cpcd.indieopen.questions.domain.SingleAnswerQuestion;
import es.upct.cpcd.indieopen.questions.domain.TrueFalseQuestion;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;

class QuestionCreator {
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final QuestionFactory questionFactory;
    private final QuestionGroupManager questionGroupManager;

    QuestionCreator(UserRepository userRepository, QuestionRepository questionRepository,
                    QuestionGroupManager questionGroupManager) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.questionGroupManager = questionGroupManager;
        this.questionFactory = new QuestionFactory();
    }

    Question createQuestion(String userId, QuestionBean questionBean) throws INDIeException {
        UserData userData = userRepository.findById(userId)
                .orElseThrow(() -> INDIeExceptionFactory.createUserNotFoundException(userId));

        try {
            // Create the question
            Question question = fillQuestion(userData, questionBean);

            // Relations
            question.setAuthor(userData);

            questionRepository.save(question);

            // Question
            return question;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw INDIeExceptionFactory.createUserRequestError(e);
        }
    }

    private Question fillQuestion(UserData userData, QuestionBean questionBean) throws INDIeException {
        Question question;

        try {

            if (questionBean.getQuestionType() == QuestionType.TRUE_FALSE) {
                question = questionFactory.createTrueFalseQuestion(questionBean.getText(), userData,
                        questionBean.isCorrect());
            } else if (questionBean.getQuestionType() == QuestionType.SINGLE) {
                List<Answer> answers = new ArrayList<>();
                for (AnswerBean answer : questionBean.getAnswersList())
                    answers.add(new Answer(answer.getText(), answer.isCorrect()));

                question = questionFactory.createSingleAnswerQuestion(questionBean.getText(), userData, answers);
            } else {
                List<Answer> answers = new ArrayList<>();
                for (AnswerBean answer : questionBean.getAnswersList())
                    answers.add(new Answer(answer.getText(), answer.isCorrect()));

                question = questionFactory.createMultipleAnswerQuestion(questionBean.getText(), userData, answers);
            }

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            throw new INDIeExceptionBuilder("question").status(Status.USER_ERROR).code(ErrorCodes.WRONG_PARAMS).build();
        }

        // Question group
        QuestionGroup group = this.questionGroupManager
                .getOrCreateQuestionGroup(userData.getId(), questionBean.getGroup())
                .orElseThrow(() -> INDIeExceptionFactory.createDuplicatedGroupException(questionBean.getGroup()));
        question.setGroup(group);

        // Tags
        question.setRawTagsFromTagsArray(questionBean.getTags());

        return question;
    }

    void updateQuestion(String authorId, String questionId, QuestionBean questionBean) throws INDIeException {
        Question question = questionRepository.findQuestionByAuthorIdAndID(authorId, questionId)
                .orElseThrow(() -> INDIeExceptionFactory.createQuestionNotFoundException(questionId));

        if (!questionBean.isAnyAnswerCorrect())
            throw new INDIeExceptionBuilder("There is not a correct answer").code(ErrorCodes.WRONG_PARAMS)
                    .status(Status.USER_ERROR).build();

        Optional<QuestionGroup> groupQuery = questionGroupManager.getOrCreateQuestionGroup(authorId,
                questionBean.getGroup());
        if (groupQuery.isEmpty())
            throw new INDIeExceptionBuilder("Group is not correct").code(ErrorCodes.WRONG_PARAMS)
                    .status(Status.USER_ERROR).build();

        List<Answer> answers = new ArrayList<>();
        for (AnswerBean answer : questionBean.getAnswers())
            answers.add(new Answer(answer.getText(), answer.isCorrect()));

        question.setQuestionText(questionBean.getText());
        question.clearAnswers();

        if (question instanceof TrueFalseQuestion) {
            TrueFalseQuestion tfq = ((TrueFalseQuestion) question);
            tfq.setCorrectAnswer(questionBean.isCorrect());
        } else if (question instanceof SingleAnswerQuestion) {
            SingleAnswerQuestion saq = ((SingleAnswerQuestion) question);
            saq.addAnswers(answers);
        } else if (question instanceof MultipleAnswerQuestion) {
            MultipleAnswerQuestion maq = ((MultipleAnswerQuestion) question);
            maq.addAnswers(answers);
        }

        question.setRawTagsFromTagsArray(questionBean.getTags());
        question.setGroup(groupQuery.get());
    }

    public List<Question> createQuestions(String userId, List<Question> questions) throws INDIeException {
        UserData userData = userRepository.findById(userId)
                .orElseThrow(() -> INDIeExceptionFactory.createUserNotFoundException(userId));

        for (Question question : questions)
            question.setAuthor(userData);

        questionRepository.saveAll(questions);

        return questions;
    }
}

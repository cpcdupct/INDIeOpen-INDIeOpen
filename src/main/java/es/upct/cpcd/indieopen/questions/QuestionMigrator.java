package es.upct.cpcd.indieopen.questions;

import org.springframework.dao.DataAccessException;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.questions.domain.Question;
import es.upct.cpcd.indieopen.questions.domain.QuestionRepository;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;

class QuestionMigrator {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    QuestionMigrator(UserRepository userRepository, QuestionRepository questionRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    Question createQuestion(String userId, Question question) throws INDIeException {
        UserData userData = userRepository.findById(userId)
                .orElseThrow(() -> INDIeExceptionFactory.createUserNotFoundException(userId));

        try {
            // Relations
            question.setAuthor(userData);

            questionRepository.save(question);

            // Question
            return question;
        } catch (DataAccessException ex) {
            throw INDIeExceptionFactory.createInternalException(ex);
        }
    }

}

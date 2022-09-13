package es.upct.cpcd.indieopen.questions;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.questions.beans.QuestionType;
import es.upct.cpcd.indieopen.questions.domain.Question;
import es.upct.cpcd.indieopen.questions.domain.QuestionRepository;
import es.upct.cpcd.indieopen.utils.validators.TypeValidator;

class QuestionFinder {

    private final QuestionRepository questionRepository;

    QuestionFinder(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    Page<Question> questionsSearch(String userId, Pageable page, String groupKey, String type) {

        if (TypeValidator.isGroupValid(groupKey) && TypeValidator.isQuestionTypeValid(type)) {
            String klass = QuestionType.getDiscriminator(type);
            return questionRepository.findQuestionByAuthorAndTypeAndGroup(userId, page, groupKey, klass);
        } else if (TypeValidator.isGroupValid(groupKey))
            return questionRepository.findQuestionByAuthorAndGroupKey(userId, page, groupKey);
        else if (TypeValidator.isQuestionTypeValid(type)) {
            String klass = QuestionType.getDiscriminator(type);
            return questionRepository.findQuestionByAuthorAndType(userId, page, klass);
        } else
            return questionRepository.findByAuthorId(userId, page);
    }

    Optional<Question> findQuestionById(String id) {
        return questionRepository.findById(id);
    }

    Question findQuestionById(String userId, String questionId) throws INDIeException {
        return questionRepository.findQuestionByAuthorIdAndID(userId, questionId)
                .orElseThrow(() -> INDIeExceptionFactory.createQuestionNotFoundException(questionId));
    }

    List<Question> findQuestions(List<String> questionIDs) {
        return questionRepository.findAllById(questionIDs);
    }

    List<Question> findQuestionsByAuthor(String userId) {
        return questionRepository.findByAuthorId(userId);
    }

}

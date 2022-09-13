package es.upct.cpcd.indieopen.questions;

import static es.upct.cpcd.indieopen.utils.LogUtils.log;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.questions.beans.QuestionBean;
import es.upct.cpcd.indieopen.questions.domain.Question;
import es.upct.cpcd.indieopen.questions.domain.QuestionGroup;
import es.upct.cpcd.indieopen.questions.domain.QuestionRepository;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.unit.domain.repository.UnitRepository;
import es.upct.cpcd.indieopen.user.domain.UserRepository;
import lombok.extern.log4j.Log4j2;

@Service
@Transactional(rollbackFor = INDIeException.class)
@Log4j2
public class QuestionService {
    private final QuestionFinder questionFinder;
    private final QuestionCreator questionCreator;
    private final QuestionRemover questionRemover;
    private final QuestionGroupManager questionGroupManager;
    private final QuestionMigrator questionMigrator;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, UserRepository userRepository,
                           DocumentDBManager documentDbManager, UnitRepository unitRepository) {
        this.questionFinder = new QuestionFinder(questionRepository);
        this.questionGroupManager = new QuestionGroupManager(questionRepository);
        this.questionCreator = new QuestionCreator(userRepository, questionRepository, questionGroupManager);
        this.questionRemover = new QuestionRemover(unitRepository, questionRepository, documentDbManager);
        this.questionMigrator = new QuestionMigrator(userRepository, questionRepository);
    }

    // Questions

    public Page<Question> findQuestionsByAuthor(String userId, Pageable page, String groupKey, String type) {
        return this.questionFinder.questionsSearch(userId, page, groupKey, type);
    }

    public List<Question> findQuestionsByAuthor(String userId) {
        return this.questionFinder.findQuestionsByAuthor(userId);
    }

    public Optional<Question> findQuestionById(String id) {
        return this.questionFinder.findQuestionById(id);
    }

    public Question findQuestionById(String userId, String questionId) throws INDIeException {
        try {
            return this.questionFinder.findQuestionById(userId, questionId);
        } catch (INDIeException e) {
            log(log, e);
            throw e;
        }
    }

    public Question createQuestion(String userId, QuestionBean questionBean) throws INDIeException {
        try {
            return this.questionCreator.createQuestion(userId, questionBean);
        } catch (INDIeException e) {
            log(log, e);
            throw e;
        }
    }

    public void updateQuestion(String authorId, String questionId, QuestionBean questionBean) throws INDIeException {
        try {
            this.questionCreator.updateQuestion(authorId, questionId, questionBean);
        } catch (INDIeException e) {
            log(log, e);
            throw e;
        }
    }

    public void deleteQuestion(String authorId, String questionId) throws INDIeException {
        try {
            this.questionRemover.deleteQuestion(authorId, questionId);
        } catch (INDIeException e) {
            log(log, e);
            throw e;
        }
    }

    // MIGRATION
    public Question createQuestion(String userId, Question question) throws INDIeException {
        try {
            return this.questionMigrator.createQuestion(userId, question);
        } catch (INDIeException e) {
            log(log, e);
            throw e;
        }
    }

    public List<Question> findQuestions(List<String> questionIDs) {
        return this.questionFinder.findQuestions(questionIDs);
    }

    public void createQuestions(String userId, List<Question> questions) throws INDIeException {
        try {
            this.questionCreator.createQuestions(userId, questions);
        } catch (INDIeException e) {
            log(log, e);
            throw e;
        }
    }

    // GROUPS
    public List<QuestionGroup> getQuestionGroupsFromUser(String currentUserId) {
        return this.questionGroupManager.getQuestionGroups(currentUserId);
    }

    public Optional<QuestionGroup> getOrCreateQuestionGroup(String userId, String group) {
        return this.questionGroupManager.getOrCreateQuestionGroup(userId, group);
    }

}

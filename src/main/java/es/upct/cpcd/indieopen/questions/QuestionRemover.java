package es.upct.cpcd.indieopen.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.dao.DataAccessException;

import es.upct.cpcd.indieopen.common.exceptions.ErrorCodes;
import es.upct.cpcd.indieopen.common.exceptions.ErrorField;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.questions.domain.Question;
import es.upct.cpcd.indieopen.questions.domain.QuestionRepository;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.domain.UnitType;
import es.upct.cpcd.indieopen.unit.domain.repository.UnitRepository;

class QuestionRemover {
    private final UnitRepository unitRepository;
    private final QuestionRepository questionRepository;
    private final DocumentDBManager documentDbManager;

    QuestionRemover(UnitRepository unitRepository, QuestionRepository questionRepository,
                    DocumentDBManager documentDbManager) {
        this.unitRepository = unitRepository;
        this.questionRepository = questionRepository;
        this.documentDbManager = documentDbManager;
    }

    void deleteQuestion(String userId, String questionId) throws INDIeException {
        Question question = questionRepository.findQuestionByAuthorIdAndID(userId, questionId)
                .orElseThrow(() -> INDIeExceptionFactory.createQuestionNotFoundException(questionId));

        // Get the question identifiers from the evaluation unit
        List<String> usedQuestionsIds = getQuestionsInEvaluationUnits(userId);

        // Ask if the question can be deleted
        if (!canDeleteQuestion(usedQuestionsIds, questionId))
            throw new INDIeExceptionBuilder("Question is already used").code(ErrorCodes.USED_QUESTION)
                    .status(Status.USER_ERROR).errorFields(ErrorField.listOf("question", "question is not valid"))
                    .build();

        // Delete the question
        try {
            questionRepository.delete(question);
        } catch (DataAccessException ex) {
            throw INDIeExceptionFactory.createInternalException(ex);
        }
    }

    /**
     * Get the list of question idenfitiers from the evaluation units of the given
     * user
     *
     * @param userId User identifier
     * @return a list of String question identifiers
     */
    private List<String> getQuestionsInEvaluationUnits(String userId) {
        List<String> questionsId = new ArrayList<>();
        List<Unit> evaluationUnits = this.unitRepository.findEvaluationUnitsByAuthorId(userId);

        for (Unit unit : evaluationUnits) {
            if (unit.hasContentCreated()) {
                Document document = documentDbManager
                        .findDocument(UnitType.collectionOf(unit.getUnitType()), unit.getDocumentId())
                        .orElseThrow(IllegalStateException::new);

                questionsId.addAll(getQuestionsIdFromDocument(document));
            }
        }

        return questionsId;
    }

    /**
     * Retrieve the IDs of the Question used in the evaluation unit content from the
     * JSON Document
     *
     * @param document JSON Document
     * @return list of question idenfitiers
     */
    private List<String> getQuestionsIdFromDocument(Document document) {
        JSONObject evUnitContent = new JSONObject(document.toJson());
        JSONArray arrayQuestions = evUnitContent.getJSONArray("evaluation");
        return StreamSupport.stream(arrayQuestions.spliterator(), false).map(obj -> ((JSONObject) obj).getString("id"))
                .collect(Collectors.toList());
    }

    /**
     * Return wether the Question with an ID can be deleted.
     *
     * @param usedQuestionsIds Group of questions id
     * @param questionId       Question to check
     * @return true if can be deleted, false otherwise
     */
    private boolean canDeleteQuestion(List<String> usedQuestionsIds, String questionId) {
        return (!usedQuestionsIds.contains(questionId));
    }
}

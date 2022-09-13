package es.upct.cpcd.indieopen.unit;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.upct.cpcd.indieopen.common.Language;
import es.upct.cpcd.indieopen.common.exceptions.ErrorCodes;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.media.MediaService;
import es.upct.cpcd.indieopen.questions.QuestionService;
import es.upct.cpcd.indieopen.questions.domain.Question;
import es.upct.cpcd.indieopen.questions.domain.QuestionGroup;
import es.upct.cpcd.indieopen.questions.resources.QuestionResource;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.services.document.DocumentDBManagerImpl;
import es.upct.cpcd.indieopen.services.document.DocumentDataException;
import es.upct.cpcd.indieopen.services.document.DocumentHelper;
import es.upct.cpcd.indieopen.unit.domain.CreativeCommons;
import es.upct.cpcd.indieopen.unit.domain.License;
import es.upct.cpcd.indieopen.unit.domain.OriginalUnitStamp;
import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.domain.UnitBuilder;
import es.upct.cpcd.indieopen.unit.domain.UnitBuilder.Build;
import es.upct.cpcd.indieopen.unit.domain.UnitMode;
import es.upct.cpcd.indieopen.unit.domain.UnitType;
import es.upct.cpcd.indieopen.unit.domain.repository.UnitRepository;
import es.upct.cpcd.indieopen.unit.dto.UnitWithModel;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;
import es.upct.cpcd.indieopen.utils.StringUtils;

class UnitShareHandler {
	private final UnitRepository unitRepository;
	private final UserRepository userRepository;
	private final DocumentDBManager documentDbManager;
	private final UnitFinder unitFinder;
	private final UnitPublisher unitPublisher;
	private final MediaService mediaService;
	private final QuestionService questionService;

	UnitShareHandler(UnitRepository unitRepository, UserRepository userRepository, DocumentDBManager documentDbManager,
			UnitFinder unitFinder, UnitPublisher unitPublisher, QuestionService questionService,
			MediaService mediaService) {
		this.unitRepository = unitRepository;
		this.unitFinder = unitFinder;
		this.unitPublisher = unitPublisher;
		this.userRepository = userRepository;
		this.documentDbManager = documentDbManager;
		this.questionService = questionService;
		this.mediaService = mediaService;
	}

	void changeLicense(String userId, int unitId, CreativeCommons ccLicense) throws INDIeException {
		Unit unit = unitFinder.findUnitByUserAndUnitId(userId, unitId);

		License license = CreativeCommons.fromLicense(ccLicense);

		if (unit.getLicense() == license && ccLicense == unit.getCreativeCommons())
			throw new INDIeExceptionBuilder("Same license").code(ErrorCodes.WRONG_PARAMS).status(Status.USER_ERROR)
					.build();

		unit.setCreativeCommons(ccLicense);

		if (license == License.PRIVATE) {
			// 1 Unpublish open resource
			this.unitPublisher.unpublishUnit(unit.getAuthor().getBase(), unit.getUnitResourceId(), true);

			// 2 Change license
			unit.setLicense(License.PRIVATE);

			// 3 Republish private version
			this.unitPublisher.publishUnit(userId, unitId);

		} else if (unit.canBeShared()) {
			// 2 Check the type of license to be applied.
			unit.setLicense(license);

			// 3 Open-publish unit
			this.unitPublisher.openPublishUnit(userId, unitId);
		} else
			throw new INDIeExceptionBuilder("Unit cannot be shared").status(Status.USER_ERROR)
					.code(ErrorCodes.WRONG_PARAMS).build();
	}

	Unit addSharedUnit(String userId, int sharedUnitId, Language language) throws INDIeException {
		// 1 Checking preconditions
		UserData author = userRepository.findById(userId)
				.orElseThrow(() -> INDIeExceptionFactory.createUserNotFoundException(userId));

		UnitWithModel unitToBeSharedWithModel = unitFinder.findUnitModelByUnitId(sharedUnitId);
		Unit unitToBeShared = unitToBeSharedWithModel.getUnit();

		if (!unitToBeShared.canBeReusedOrCopied()) {
			throw new INDIeExceptionBuilder("Unit cannot be shared").status(Status.USER_ERROR)
					.code(ErrorCodes.WRONG_PARAMS).build();
		}

		if (hasSharedUnit(author, unitToBeShared)) {
			throw new INDIeExceptionBuilder("Unit existing").status(Status.USER_ERROR)
					.code(ErrorCodes.UNIT_ALREADY_ADDED).build();
		}

		if (unitToBeShared.getAuthor().equals(author))
			throw new INDIeExceptionBuilder("Unit has the same author").status(Status.USER_ERROR)
					.code(ErrorCodes.WRONG_PARAMS).build();

		// 2 Create units
		Unit sharedUnit = getSuperficialCopyOfUnit(author, unitToBeShared);
		unitToBeShared.getSharedBy().add(sharedUnit);

		// 3 Create document depending on the type
		Document document = DocumentHelper.createEmptyUnitDocument(sharedUnit.getUnitType());

		updateDocumentByType(language, author, unitToBeSharedWithModel, unitToBeShared, document);

		try {
			sharedUnit.setDocumentId(
					documentDbManager.storeDocument(UnitType.collectionOf(sharedUnit.getUnitType()), document));
		} catch (DocumentDataException e) {
			throw INDIeExceptionFactory.createInternalException(e);
		}

		// Save to the repositories
		unitRepository.save(sharedUnit);

		// Publish unit
		this.unitPublisher.publishUnit(author.getId(), sharedUnit.getId());

		// If it is a reused content unit, we must SYNC the media
		this.mediaService.syncrhonizeMediaResourcesWithSavedUnit(author.getId(), unitToBeShared.getId(),
				unitToBeSharedWithModel.getJSONObjectFromDocument().getJSONArray("sections"));

		return sharedUnit;
	}

	private boolean hasSharedUnit(UserData author, Unit unitToBeShared) {
		return this.unitRepository.findByAuthorId(author.getId()).stream().anyMatch(
				u -> u.getOriginalUnit() != null && (u.getOriginalUnit().getId().equals(unitToBeShared.getId())));

	}

	private void updateDocumentByType(Language language, UserData author, UnitWithModel unitToBeSharedWithModel,
			Unit unitToBeShared, Document document) throws INDIeException {
		if (unitToBeShared.getUnitType() == UnitType.CONTENT) {
			DocumentHelper.updateContentDocument(document,
					unitToBeSharedWithModel.getJSONObjectFromDocument().getJSONArray("sections"),
					unitToBeSharedWithModel.getJSONObjectFromDocument().getInt("version"));
		} else if (Unit.getModeFromLicense(unitToBeShared.getLicense()) == UnitMode.REUSED) {
			// Extract the IDs of existing questions
			String[] questionIds = extractIds(unitToBeSharedWithModel.getJSONObjectFromDocument());
			// Create a group
			QuestionGroup group = questionService
					.getOrCreateQuestionGroup(author.getId(), QuestionGroup.getReusedGroupName(language))
					.orElseThrow(IllegalArgumentException::new);
			// Get the Question objects
			List<Question> questionsFromSharedUnit = extractQuestions(questionIds, group);
			// Create the new questions, which are a copy
			for (Question question : questionsFromSharedUnit)
				question.setAuthor(author);
			// Create the evaluation unit in the exact same order
			JSONArray questionsInUnit = getJSONArrayFromQuestions(questionsFromSharedUnit);
			// Update the document with the unit
			DocumentHelper.updateEvaluationDocument(document, questionsInUnit);
		} else {
			JSONArray arrayOfQuestions = unitToBeSharedWithModel.getJSONObjectFromDocument().getJSONArray("evaluation");
			document.append("evaluation", DocumentDBManagerImpl.transformArrayIntoDBObjectList(arrayOfQuestions));
		}
	}

	private String[] extractIds(JSONObject evaluationDocument) {
		JSONArray arrayOfQuestions = evaluationDocument.getJSONArray("evaluation");

		String[] ids = new String[arrayOfQuestions.length()];

		for (int i = 0; i < arrayOfQuestions.length(); i++) {
			JSONObject question = arrayOfQuestions.getJSONObject(i);
			ids[i] = question.getString("id");
		}

		return ids;
	}

	private List<Question> extractQuestions(String[] arrayOfQuestionId, QuestionGroup group) throws INDIeException {
		List<Question> questions = new ArrayList<>();

		for (String id : arrayOfQuestionId) {
			Question question = questionService.findQuestionById(id)
					.orElseThrow(() -> INDIeExceptionFactory.createQuestionNotFoundException(id));
			Question newQuestion = question.getCopyOfQuestion();
			newQuestion.setGroup(group);
			questions.add(newQuestion);
		}

		return questions;
	}

	private JSONArray getJSONArrayFromQuestions(List<Question> questionsFromSharedUnit) {
		JSONArray array = new JSONArray();
		try {
			ObjectMapper mapper = new ObjectMapper();

			for (Question question : questionsFromSharedUnit) {
				QuestionResource resource = QuestionResource.fromQuestion(question);
				JSONObject questionJObject = new JSONObject(mapper.writeValueAsString(resource));
				array.put(questionJObject);
			}
		} catch (JSONException | JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}

		return array;
	}

	private Unit getSuperficialCopyOfUnit(UserData author, Unit unitToBeShared) {
		// Mandatory params
		Build buildUnit = UnitBuilder.createBuilder().withAuthor(author).withType(unitToBeShared.getUnitType())
				.withUnitInfo(unitToBeShared.getName(), unitToBeShared.getShortDescription(),
						unitToBeShared.getLanguage())
				.withCategory(unitToBeShared.getCategory()).withDefaultLicense()
				.withMode(Unit.getModeFromLicense(unitToBeShared.getLicense()))
				.withEducationalContext(unitToBeShared.getEducationalContexts()).withDefaultCreativeCommons();

		// Optional params
		buildUnit.withAverageRating(0).withCover(unitToBeShared.getCover())
				.withOriginalUnitStamp(OriginalUnitStamp.from(unitToBeShared)).withOriginalUnit(unitToBeShared)
				.withAgeRange(unitToBeShared.getAgeRange().getMin(), unitToBeShared.getAgeRange().getMax())
				.withTheme(unitToBeShared.getTheme());

		if (StringUtils.isStringValid(unitToBeShared.getLongDescription()))
			buildUnit.withLongDescription(unitToBeShared.getLongDescription());

		if (unitToBeShared.getTags().length > 0)
			buildUnit.withTags(unitToBeShared.getTags());

		// Build
		return buildUnit.build();
	}
}

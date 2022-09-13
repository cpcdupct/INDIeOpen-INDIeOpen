package es.upct.cpcd.indieopen.unit;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.bson.Document;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import es.upct.cpcd.indieopen.category.domain.CategoryRepository;
import es.upct.cpcd.indieopen.common.Language;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.resources.LTIUser;
import es.upct.cpcd.indieopen.educationalcontext.domain.EducationalContextRepository;
import es.upct.cpcd.indieopen.media.MediaService;
import es.upct.cpcd.indieopen.questions.QuestionService;
import es.upct.cpcd.indieopen.rate.RatingService;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.services.publish.PublishService;
import es.upct.cpcd.indieopen.services.transform.TransformService;
import es.upct.cpcd.indieopen.token.TokenParser;
import es.upct.cpcd.indieopen.unit.beans.CreateUnitBean;
import es.upct.cpcd.indieopen.unit.beans.UpdateUnitInfoBean;
import es.upct.cpcd.indieopen.unit.domain.CreativeCommons;
import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.domain.repository.UnitRepository;
import es.upct.cpcd.indieopen.unit.dto.UnitWithModel;
import es.upct.cpcd.indieopen.unit.web.resources.OriginalUnitStatusResource;
import es.upct.cpcd.indieopen.unit.web.resources.UnitResource;
import es.upct.cpcd.indieopen.user.domain.UserRepository;

@Service
@Validated
@Transactional(rollbackFor = INDIeException.class, propagation = Propagation.REQUIRED)
public class UnitService {
	// Modules
	private final UnitCreator unitCreator;
	private final UnitPublisher unitPublisher;
	private final UnitFinder unitFinder;
	private final UnitModelHandler unitModelHanlder;
	private final UnitShareHandler unitShareHandler;
	private final UnitRemover unitRemover;
	private final UnitAccessHandler unitAccessHandler;
	private final UnitModelAccess unitModelAccess;

	@Autowired
	public UnitService(UserRepository userRepository, UnitRepository unitRepository,
			CategoryRepository categoryRepository, DocumentDBManager documentDbManager, PublishService publishService,
			TransformService transformService, QuestionService questionService, TokenParser tokenParser,
			MediaService mediaService, EducationalContextRepository educationalContextRepository,
			RatingService ratingService) {
		// Modules
		this.unitFinder = new UnitFinder(unitRepository, documentDbManager);
		this.unitCreator = new UnitCreator(userRepository, unitRepository, categoryRepository, documentDbManager,
				educationalContextRepository);

		// MOdules with dependencies
		this.unitPublisher = new UnitPublisher(publishService, transformService, unitFinder, documentDbManager);
		this.unitModelAccess = new UnitModelAccess(transformService, unitFinder);

		this.unitRemover = new UnitRemover(documentDbManager, mediaService, unitFinder, userRepository, unitRepository,
				publishService, ratingService);
		this.unitModelHanlder = new UnitModelHandler(unitFinder, tokenParser, documentDbManager, mediaService);
		this.unitShareHandler = new UnitShareHandler(unitRepository, userRepository, documentDbManager, unitFinder,
				unitPublisher, questionService, mediaService);
		this.unitAccessHandler = new UnitAccessHandler(unitRepository, userRepository);
	}

	public void setPublishService(PublishService publishService) {
		this.unitPublisher.setPublishService(publishService);
		this.unitRemover.setPublishService(publishService);
	}

	public void setTransformService(TransformService transformService) {
		this.unitPublisher.setTransformService(transformService);
	}

	public Unit createUnit(String userId, @Valid CreateUnitBean createUnitBean) throws INDIeException {
		return this.unitCreator.createUnit(userId, createUnitBean);
	}

	public void createSampleUnit(String userId, Unit unit) throws INDIeException, IOException {
		this.unitCreator.createSampleUnit(userId, unit);
	}

	public UnitResource updateUnit(String userId, int unitId, @Valid UpdateUnitInfoBean updateUnitBean)
			throws INDIeException {
		return UnitResource.fromUnit(this.unitCreator.updateUnit(userId, unitId, updateUnitBean));
	}

	public UnitResource findUnitResourceByUserAndId(String userId, int unitId) throws INDIeException {
		Unit unit = findUnitByUserAndId(userId, unitId);
		return UnitResource.fromUnit(unit);
	}

	public Unit findUnitByUserAndId(String userId, int unitId) throws INDIeException {
		return this.unitFinder.findUnitByUserAndUnitId(userId, unitId);
	}

	public Page<UnitResource> findUnitsByUser(String userId, Pageable page, String type) {
		return this.unitFinder.findUnitsByUser(userId, page, type).map(UnitResource::fromUnit);
	}

	public String publishUnit(String userId, int unitId) throws INDIeException {
		return this.unitPublisher.publishUnit(userId, unitId);
	}

	public String previewUnit(String userId, int unitId) throws INDIeException {
		return this.unitPublisher.previewUnit(userId, unitId);
	}

	// SHARE UNITS
	public void changeLicense(String userId, int unitId, CreativeCommons ccLicense) throws INDIeException {
		this.unitShareHandler.changeLicense(userId, unitId, ccLicense);
	}

	// INDIE EDITOR
	public String generateToken(String userId, int unitId) throws INDIeException {
		return this.unitModelHanlder.generateToken(userId, unitId);
	}

	public UnitWithModel getModelByUnitId(String userId, int unitId) throws INDIeException {
		return this.unitFinder.findUnitModelByUserAndUnitId(userId, unitId);
	}

	public void saveModelOfUnit(String userId, int unitId, JSONObject unitContent) throws INDIeException {
		this.unitModelHanlder.saveModelOfUnit(userId, unitId, unitContent);
	}

	public Unit addSharedUnit(String userId, int sharedUnitId, Language language) throws INDIeException {
		return this.unitShareHandler.addSharedUnit(userId, sharedUnitId, language);
	}

	// MIGRATION
	public Unit findunitById(int unitId) throws INDIeException {
		return this.unitFinder.findUnitById(unitId);
	}

	public void deleteUnit(String userId, int unitId) throws INDIeException {
		this.unitRemover.deleteUnit(userId, unitId);
	}

	public Unit findUnitsCriteria(int unitId, String... relations) throws INDIeException {
		return this.unitFinder.findUnitsCriteria(unitId, relations);
	}

	public Unit createUnit(Unit unit, Document document) throws INDIeException {
		return this.unitCreator.createUnit(unit, document);
	}

	public List<Unit> findAllUnitsByUser(String user) {
		return this.unitFinder.findAllUnitsByUser(user);
	}

	public OriginalUnitStatusResource getOriginalUnitStatus(String userId, int unitId) throws INDIeException {
		return this.unitModelHanlder.getOriginalUnitStatus(userId, unitId);
	}

	public void updateVersionUnit(String userId, int unitId) throws INDIeException {
		this.unitPublisher.updateVersionUnit(userId, unitId);
	}

	public void toggleLearningAnalytics(String userId, int unitId, boolean analytics) throws INDIeException {
		this.unitPublisher.toggleLearningAnalytics(userId, unitId, analytics);
	}

	// ACCESS
	public List<LTIUser> findUnitAccesesForUnit(String userId, int unitId) throws INDIeException {
		return this.unitAccessHandler.findUnitAccesesForUnit(userId, unitId);
	}

	public boolean getAuthorizationForUnit(String unitResource, String email) throws INDIeException {
		return this.unitAccessHandler.getAuthorizationForUnit(unitResource, email);
	}

	public void putUnitAccesesForUnit(String userId, int unitId, List<String> emails) throws INDIeException {
		this.unitAccessHandler.putUnitAccesesForUnit(userId, unitId, emails);
	}

	public List<Unit> findAllPublishedUnits(String userId) {
		return this.unitFinder.findAllPublishedUnits(userId);
	}

	public String getModelByUnitResourceId(String resource) throws INDIeException {
		return this.unitModelAccess.getModelByUnitResourceId(resource);
	}

}

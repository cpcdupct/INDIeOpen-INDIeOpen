package es.upct.cpcd.indieopen.unit;

import java.time.LocalDateTime;

import org.json.JSONArray;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.services.document.DocumentDBCollection;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.services.document.DocumentDBManagerImpl;
import es.upct.cpcd.indieopen.services.document.DocumentDataException;
import es.upct.cpcd.indieopen.services.document.DocumentHelper;
import es.upct.cpcd.indieopen.services.publish.PublishService;
import es.upct.cpcd.indieopen.services.publish.PublishServiceException;
import es.upct.cpcd.indieopen.services.transform.TransformMode;
import es.upct.cpcd.indieopen.services.transform.TransformService;
import es.upct.cpcd.indieopen.services.transform.TransformServiceException;
import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.domain.UnitType;
import es.upct.cpcd.indieopen.unit.dto.UnitWithModel;

class UnitPublisher {
	private PublishService publishService;
	private final UnitFinder unitFinder;
	private TransformService transformService;
	private DocumentDBManager documentDbManager;

	UnitPublisher(PublishService publishService, TransformService transformService, UnitFinder unitFinder,
			DocumentDBManager documentDbManager) {
		this.publishService = publishService;
		this.unitFinder = unitFinder;
		this.transformService = transformService;
		this.documentDbManager = documentDbManager;
	}

	String previewUnit(String userId, int unitId) throws INDIeException {
		UnitWithModel unitWithModel = this.unitFinder.findUnitModelByUserAndUnitId(userId, unitId);

		try {
			String model = transformService.transformUnit(unitWithModel, TransformMode.PREVIEW);

			return publishService.previewUnit(model, unitWithModel.getUnit().getAuthor().getBase(),
					unitWithModel.getUnit().getUnitType());
		} catch (PublishServiceException | TransformServiceException e) {
			throw INDIeExceptionFactory.createInternalException(e);
		}
	}

	String publishUnit(String userId, int unitId) throws INDIeException {
		UnitWithModel unitWithModel = this.unitFinder.findUnitModelByUserAndUnitId(userId, unitId);
		Unit unit = unitWithModel.getUnit();

		try {
			String model = transformService.transformUnit(unitWithModel, TransformMode.INTEROPERABILITY);
			publishService.publishUnit(model, unit.getAuthor().getBase(), unit.getUnitResourceId(), unit.getUnitType());

			if (unit.canBeReusedOrCopied()) {
				model = transformService.transformUnit(unitWithModel, TransformMode.OPEN);
				publishService.openPublishUnit(model, unit.getAuthor().getBase(), unit.getUnitResourceId(),
						unit.getUnitType());
			}

			unit.setPublishedDate(LocalDateTime.now());
			unit.setDraft(false);

			return unit.getResource();
		} catch (PublishServiceException | TransformServiceException e) {
			throw INDIeExceptionFactory.createInternalException(e);
		}
	}

	String openPublishUnit(String userId, int unitId) throws INDIeException {
		UnitWithModel unitWithModel = this.unitFinder.findUnitModelByUserAndUnitId(userId, unitId);
		Unit unit = unitWithModel.getUnit();

		try {
			String model = transformService.transformUnit(unitWithModel, TransformMode.OPEN);
			publishService.openPublishUnit(model, unit.getAuthor().getBase(), unit.getUnitResourceId(),
					unit.getUnitType());
			unit.setPublishedDate(LocalDateTime.now());

			return unit.getResource();
		} catch (PublishServiceException | TransformServiceException e) {
			throw INDIeExceptionFactory.createInternalException(e);
		}
	}

	void unpublishUnit(String userBase, String unitResource, boolean isUnitPublic) throws INDIeException {
		try {
			publishService.unpublishUnit(userBase, unitResource, isUnitPublic);
		} catch (PublishServiceException e) {
			throw INDIeExceptionFactory.createInternalException(e);
		}
	}

	void setPublishService(PublishService publishService) {
		this.publishService = publishService;
	}

	void setTransformService(TransformService transformService) {
		this.transformService = transformService;
	}

	public void updateVersionUnit(String userId, int unitId) throws INDIeException {
		UnitWithModel unitWithModel = this.unitFinder.findUnitModelByUserAndUnitId(userId, unitId);

		if (unitWithModel.getUnit().getOriginalUnit() == null
				&& (!unitWithModel.getUnit().getOriginalUnit().canBeReusedOrCopied()))
			throw new INDIeExceptionBuilder("Original unit not found").code("ORIGINAL_UNIT_NOT_FOUND")
					.status(Status.USER_ERROR).build();

		UnitWithModel originalUnitWithModel = this.unitFinder.findUnitModelByUnitId(unitId);

		DocumentDBCollection collection;

		if (unitWithModel.getUnit().getUnitType() == UnitType.CONTENT) {
			DocumentHelper.updateContentDocument(unitWithModel.getDocument(),
					originalUnitWithModel.getJSONObjectFromDocument().getJSONArray("sections"),
					originalUnitWithModel.getJSONObjectFromDocument().getInt("version"));
			collection = DocumentDBCollection.CONTENT_UNITS;
		} else {
			JSONArray arrayOfQuestions = originalUnitWithModel.getJSONObjectFromDocument().getJSONArray("evaluation");
			unitWithModel.getDocument().replace("evaluation",
					DocumentDBManagerImpl.transformArrayIntoDBObjectList(arrayOfQuestions));
			collection = DocumentDBCollection.EVALUATION_UNITS;
		}

		unitWithModel.getUnit().setDraft(true);
		unitWithModel.getUnit().getOriginalUnitStamp().setTimeStamp(LocalDateTime.now());

		try {
			documentDbManager.replaceDocument(collection, unitWithModel.getUnit().getDocumentId(),
					unitWithModel.getDocument());
		} catch (DocumentDataException e) {
			throw INDIeExceptionFactory.createInternalException(e);
		}
	}

	public void toggleLearningAnalytics(String userId, int unitId, boolean analytics) throws INDIeException {
		UnitWithModel unitWithModel = this.unitFinder.findUnitModelByUserAndUnitId(userId, unitId);
		Unit unit = unitWithModel.getUnit();
		unit.setAnalytics(analytics);

		try {
			String model = transformService.transformUnit(unitWithModel, TransformMode.INTEROPERABILITY);
			publishService.publishUnit(model, unit.getAuthor().getBase(), unit.getUnitResourceId(), unit.getUnitType());
		} catch (PublishServiceException | TransformServiceException e) {
			throw INDIeExceptionFactory.createInternalException(e);
		}
	}

}

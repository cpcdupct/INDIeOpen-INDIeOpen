package es.upct.cpcd.indieopen.unit;

import org.bson.Document;
import org.json.JSONObject;

import es.upct.cpcd.indieopen.common.exceptions.ErrorCodes;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.media.MediaService;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.services.document.DocumentHelper;
import es.upct.cpcd.indieopen.token.ContentType;
import es.upct.cpcd.indieopen.token.TokenParser;
import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.domain.UnitType;
import es.upct.cpcd.indieopen.unit.dto.UnitWithModel;
import es.upct.cpcd.indieopen.unit.web.resources.OriginalUnitStatusResource;
import es.upct.cpcd.indieopen.utils.DateUtils;

class UnitModelHandler {
	private final DocumentDBManager documentDbManager;
	private final UnitFinder unitFinder;
	private final TokenParser tokenParser;
	private final MediaService mediaService;

	UnitModelHandler(UnitFinder unitFinder, TokenParser tokenParser, DocumentDBManager documentDbManager,
			MediaService mediaService) {
		this.unitFinder = unitFinder;
		this.documentDbManager = documentDbManager;
		this.tokenParser = tokenParser;
		this.mediaService = mediaService;
	}

	void saveModelOfUnit(String userId, int unitId, JSONObject content) throws INDIeException {
		UnitWithModel unitWithModel = unitFinder.findUnitModelByUserAndUnitId(userId, unitId);
		Unit unit = unitWithModel.getUnit();
		Document document = unitWithModel.getDocument();

		if (!unit.hasContentCreated()) {
			throw new INDIeExceptionBuilder("Unit cannot be updated because it is copied").status(Status.USER_ERROR)
					.code(ErrorCodes.WRONG_PARAMS).build();
		}

		try {
			if (unit.getUnitType() == UnitType.CONTENT) {
				this.mediaService.syncrhonizeMediaResourcesWithSavedUnit(userId, unitId,
						content.getJSONArray("sections"));
				DocumentHelper.updateContentDocument(document, content.getJSONArray("sections"),
						content.getInt("version"));
			} else if (unit.getUnitType() == UnitType.EVALUATION)
				DocumentHelper.updateEvaluationDocument(document, content.getJSONArray("evaluation"));

			unit.setDraft(true);

			documentDbManager.replaceDocument(UnitType.collectionOf(unit.getUnitType()), unit.getDocumentId(),
					document);
		} catch (Exception e) {
			throw INDIeExceptionFactory.createInternalException(e);
		}
	}

	String generateToken(String userId, int unitId) throws INDIeException {
		Unit unit = unitFinder.findUnitByUserAndUnitId(userId, unitId);
		return this.tokenParser.generateToken(unitId, userId, ContentType.get(unit.getUnitType().getValue()));
	}

	public OriginalUnitStatusResource getOriginalUnitStatus(String userId, int unitId) throws INDIeException {
		Unit unit = unitFinder.findUnitByUserAndUnitId(userId, unitId);

		if (!unit.isOriginal() && unit.getOriginalUnit() != null) {
			Unit originalUnit = unit.getOriginalUnit();

			if (!originalUnit.canBeReusedOrCopied())
				return new OriginalUnitStatusResource(DateUtils.dateToISOString(originalUnit.getPublishedDate()), false,
						originalUnit.getResource());

			return new OriginalUnitStatusResource(DateUtils.dateToISOString(originalUnit.getPublishedDate()), true,
					originalUnit.getResource());
		}

		return new OriginalUnitStatusResource(null, false, null);

	}
}

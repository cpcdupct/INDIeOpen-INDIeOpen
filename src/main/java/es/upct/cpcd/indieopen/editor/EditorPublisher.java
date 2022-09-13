package es.upct.cpcd.indieopen.editor;

import es.upct.cpcd.indieopen.common.exceptions.ErrorCodes;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.services.publish.PublishService;
import es.upct.cpcd.indieopen.services.publish.PublishServiceException;
import es.upct.cpcd.indieopen.token.ModelToken;
import es.upct.cpcd.indieopen.token.TokenParser;
import es.upct.cpcd.indieopen.unit.UnitService;
import es.upct.cpcd.indieopen.unit.domain.Unit;

class EditorPublisher {

	private final TokenParser tokenParser;
	private final PublishService publishService;
	private final UnitService unitService;

	EditorPublisher(TokenParser tokenParser, PublishService publishService, UnitService unitService) {
		this.tokenParser = tokenParser;
		this.publishService = publishService;
		this.unitService = unitService;
	}

	String preview(String token, String plainModel) throws INDIeException {
		ModelToken modelToken = tokenParser.parseToken(token);
		Unit unit = unitService.findunitById(modelToken.getEntity());

		if (!sameType(modelToken, unit)) {
			throw new INDIeExceptionBuilder("Not same type of token").code(ErrorCodes.EDITOR_TOKEN_NOT_VALID)
					.status(Status.USER_ERROR).build();
		}

		String userBase = unit.getAuthor().getBase();

		try {
			return publishService.previewUnit(plainModel, userBase, unit.getUnitType());
		} catch (PublishServiceException e) {
			e.printStackTrace();
			throw INDIeExceptionFactory.createInternalException(e);
		}
	}

	private boolean sameType(ModelToken modelToken, Unit unit) {
		return (modelToken.getType().getValue().equals(unit.getUnitType().getValue()));
	}
}

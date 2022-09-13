package es.upct.cpcd.indieopen.unit;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.services.transform.TransformMode;
import es.upct.cpcd.indieopen.services.transform.TransformService;
import es.upct.cpcd.indieopen.services.transform.TransformServiceException;
import es.upct.cpcd.indieopen.unit.dto.UnitWithModel;

class UnitModelAccess {

	private final TransformService transformService;
	private final UnitFinder unitFinder;

	UnitModelAccess(TransformService transformService, UnitFinder unitFinder) {
		this.transformService = transformService;
		this.unitFinder = unitFinder;
	}

	String getModelByUnitResourceId(String resource) throws INDIeException {
		UnitWithModel unitWithModel = this.unitFinder.findUnitModelByResourceId(resource);
		try {
			return transformService.transformUnit(unitWithModel, TransformMode.OPEN);
		} catch (TransformServiceException e) {
			throw INDIeExceptionFactory.createInternalException(e);
		}
	}
}

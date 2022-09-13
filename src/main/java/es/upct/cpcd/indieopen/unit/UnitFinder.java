package es.upct.cpcd.indieopen.unit;

import static es.upct.cpcd.indieopen.utils.validators.TypeValidator.isTypeValid;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.domain.UnitType;
import es.upct.cpcd.indieopen.unit.domain.repository.UnitRepository;
import es.upct.cpcd.indieopen.unit.dto.UnitWithModel;

public class UnitFinder {
	private final UnitRepository unitRepository;
	private final DocumentDBManager documentDbManager;

	UnitFinder(UnitRepository unitRepository, DocumentDBManager documentDbManager) {
		this.unitRepository = unitRepository;
		this.documentDbManager = documentDbManager;
	}

	Unit findUnitById(int unitId) throws INDIeException {
		return unitRepository.findById(unitId).orElseThrow(() -> INDIeExceptionFactory.createUnitNotFound(unitId));
	}

	Unit findUnitByUserAndUnitId(String userId, int unitId) throws INDIeException {
		return unitRepository.findByIdAndAuthorId(unitId, userId)
				.orElseThrow(() -> INDIeExceptionFactory.createUnitNotFound(unitId));
	}

	Page<Unit> findUnitsByUser(String userId, Pageable page, String type) {
		if (!isTypeValid(type))
			return unitRepository.findByAuthorId(userId, page);
		else
			return unitRepository.findByAuthorIdAndUnitType(page, userId, UnitType.get(type));

	}

	UnitWithModel findUnitModelByUserAndUnitId(String userId, int unitId) throws INDIeException {
		Unit unit = unitRepository.findByIdAndAuthorId(unitId, userId)
				.orElseThrow(() -> INDIeExceptionFactory.createUnitNotFound(unitId));

		Document document = documentDbManager
				.findDocument(UnitType.collectionOf(unit.getUnitType()), unit.getDocumentId())
				.orElseThrow(() -> INDIeExceptionFactory.createInternalException(("no content found for " + unitId)));

		return new UnitWithModel(unit, document);
	}

	UnitWithModel findUnitModelByUnitId(int unitId) throws INDIeException {
		Unit unit = unitRepository.findUnitById(unitId)
				.orElseThrow(() -> INDIeExceptionFactory.createUnitNotFound(unitId));

		Document document = documentDbManager
				.findDocument(UnitType.collectionOf(unit.getUnitType()), unit.getDocumentId())
				.orElseThrow(() -> INDIeExceptionFactory.createInternalException(("no content found for " + unitId)));

		return new UnitWithModel(unit, document);
	}

	public Unit findUnitsCriteria(int unitId, String[] relations) throws INDIeException {
		Unit unit = unitRepository.findUnitById(unitId)
				.orElseThrow(() -> INDIeExceptionFactory.createUnitNotFound(unitId));

		List<String> relationsList = Arrays.asList(relations);

		if (relationsList.contains("sharedBy"))
			unit.getSharedBy().size();

		return unit;
	}

	UnitWithModel findUnitModelByResourceId(String resource) throws INDIeException {
		Unit unit = unitRepository.findByUnitResourceId(resource)
				.orElseThrow(() -> INDIeExceptionFactory.createUnitResourceNotFound(resource));
		
		Document document = documentDbManager
				.findDocument(UnitType.collectionOf(unit.getUnitType()), unit.getDocumentId())
				.orElseThrow(() -> INDIeExceptionFactory.createInternalException(("no content found for " + resource)));
		
		return new UnitWithModel(unit, document);
	}

	public List<Unit> findAllUnitsByUser(String user) {
		return this.unitRepository.findByAuthorId(user);
	}

	public List<Unit> findAllPublishedUnits(String userId) {
		return this.unitRepository.findAllPublishedUnitsByUser(userId);
	}

}

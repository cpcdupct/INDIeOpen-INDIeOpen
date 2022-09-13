package es.upct.cpcd.indieopen.unit;

import java.util.List;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.media.MediaService;
import es.upct.cpcd.indieopen.rate.RatingService;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.services.document.DocumentDataException;
import es.upct.cpcd.indieopen.services.publish.PublishService;
import es.upct.cpcd.indieopen.services.publish.PublishServiceException;
import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.domain.UnitType;
import es.upct.cpcd.indieopen.unit.domain.repository.UnitRepository;
import es.upct.cpcd.indieopen.unit.dto.UnitWithModel;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;

class UnitRemover {
	private final DocumentDBManager documentDbManager;
	private final MediaService mediaService;
	private final UnitFinder unitFinder;
	private final UserRepository userRepository;
	private final UnitRepository unitRepository;
	private PublishService publishService;
	private final RatingService ratingService;

	public UnitRemover(DocumentDBManager documentDbManager, MediaService mediaService, UnitFinder unitFinder,
			UserRepository userRepository, UnitRepository unitRepository, PublishService publishService,
			RatingService ratingService) {
		this.documentDbManager = documentDbManager;
		this.mediaService = mediaService;
		this.unitFinder = unitFinder;
		this.userRepository = userRepository;
		this.unitRepository = unitRepository;
		this.publishService = publishService;
		this.ratingService = ratingService;
	}

	void deleteUnit(String userId, int unitId) throws INDIeException {
		UserData author = userRepository.findById(userId)
				.orElseThrow(() -> INDIeExceptionFactory.createUserNotFoundException(userId));

		UnitWithModel unitWithModel = this.unitFinder.findUnitModelByUserAndUnitId(userId, unitId);
		Unit unit = unitWithModel.getUnit();

		if (unit.isOriginal()) {
			List<Unit> unitsShared = unit.getSharedBy();
			for (Unit unitShared : unitsShared) {
				unitShared.setOriginalUnit(null);
			}
			unit.getSharedBy().clear();
			this.ratingService.deleteRatings(unit.getId());
		} else {
			unit.getOriginalUnit().getSharedBy().remove(unit);
		}

		try {
			if (unit.isPublished())
				publishService.unpublishUnit(author.getBase(), unit.getUnitResourceId(), false);
			if (unit.isOpen())
				publishService.unpublishUnit(author.getBase(), unit.getUnitResourceId(), true);

		} catch (PublishServiceException e) {
			throw INDIeExceptionFactory.createInternalException(e);
		}

		try {
			documentDbManager.deleteDocument(UnitType.collectionOf(unit.getUnitType()), unit.getDocumentId());

		} catch (DocumentDataException e) {
			throw INDIeExceptionFactory.createInternalException(e);
		}

		if (unit.getUnitType() == UnitType.CONTENT)
			mediaService.syncrhonizeMediaResourcesWithDeletedUnit(userId, unit.getId(),
					unitWithModel.getDocumentJSONObject().getJSONArray("sections"));

		unitRepository.delete(unit);
	}

	public void setPublishService(PublishService publishService) {
		this.publishService = publishService;
	}
}

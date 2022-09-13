package es.upct.cpcd.indieopen.rate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.rate.domain.Rate;
import es.upct.cpcd.indieopen.rate.domain.repository.RatingRepository;
import es.upct.cpcd.indieopen.rate.request.AddRatingRequest;
import es.upct.cpcd.indieopen.rate.resources.RatingAverageResource;
import es.upct.cpcd.indieopen.rate.resources.RatingResource;
import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.domain.repository.UnitRepository;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;

@Service
@Transactional(rollbackFor = INDIeException.class)
@Validated
public class RatingService {

	private final RatingRepository ratingRepository;
	private final UnitRepository unitRepository;
	private final UserRepository userRepository;

	@Autowired
	public RatingService(RatingRepository ratingRepository, UnitRepository unitRepository,
			UserRepository userRepository) {
		this.ratingRepository = ratingRepository;
		this.unitRepository = unitRepository;
		this.userRepository = userRepository;
	}

	public Page<RatingResource> getRatingsByUnit(int unitId, Pageable page) {
		return this.ratingRepository.findByUnitId(unitId, page).map(RatingResource::from);
	}

	public RatingResource createRating(String userId, int unitId, AddRatingRequest request) throws INDIeException {
		UserData author = this.userRepository.findById(userId)
				.orElseThrow(() -> INDIeExceptionFactory.createUserNotFoundException(userId));

		Unit unit = this.unitRepository.findById(unitId)
				.orElseThrow(() -> INDIeExceptionFactory.createUnitNotAccessible(unitId));

		// Check if rating exists
		List<Rate> allRatingsForUnit = ratingRepository.findByUnitId(unitId);
		if (existRating(userId, unitId, allRatingsForUnit))
			throw INDIeExceptionFactory.createUserRequestError("rating");

		// Create rating
		Rate rating = new Rate(author, unit, request.getRating());
		ratingRepository.save(rating);
		allRatingsForUnit.add(rating);

		// Refresh ratings
		unit.refreshRatingAverage(allRatingsForUnit);

		// Create resource
		return RatingResource.from(rating);
	}

	private boolean existRating(String userId, int unitId, List<Rate> allRatingsForUnit) {
		return allRatingsForUnit.stream()
				.anyMatch(r -> r.getUnit().getId() == unitId && r.getAuthor().getId().equals(userId));
	}

	public RatingResource findRating(String userId, int unitId) throws INDIeException {
		return RatingResource.from(this.ratingRepository.findByUnitIdAndAuthorId(unitId, userId)
				.orElseThrow(() -> INDIeExceptionFactory.createEntityNotFound("rating")));
	}

	public RatingAverageResource findAverageRatingByUnit(int unitId) throws INDIeException {
		Unit unit = unitRepository.findById(unitId)
				.orElseThrow(() -> INDIeExceptionFactory.createUnitNotAccessible(unitId));

		return RatingAverageResource.from(unit);
	}

	public void deleteRatings(int unitId) {
		List<Rate> ratings = ratingRepository.findByUnitId(unitId);
		this.ratingRepository.deleteAll(ratings);
	}
}

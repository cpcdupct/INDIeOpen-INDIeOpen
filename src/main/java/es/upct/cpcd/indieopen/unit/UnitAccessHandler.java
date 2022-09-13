package es.upct.cpcd.indieopen.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import es.upct.cpcd.indieopen.common.exceptions.ErrorField;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.resources.LTIUser;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.unit.domain.AuthorizacionAccess;
import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.domain.repository.UnitRepository;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;
import es.upct.cpcd.indieopen.utils.StringUtils;

class UnitAccessHandler {

	private UnitRepository unitRepository;
	private UserRepository userRepository;

	UnitAccessHandler(UnitRepository unitRepository, UserRepository userRepository) {
		this.unitRepository = unitRepository;
		this.userRepository = userRepository;
	}

	boolean getAuthorizationForUnit(String unitResource, String email) throws INDIeException {
		Unit unit = unitRepository.findByUnitResourceId(unitResource)
				.orElseThrow(() -> new INDIeExceptionBuilder("Unit access not authorized").status(Status.UNAUTHORIZED)
						.errorFields(ErrorField.listOf(unitResource, "Not authorized")).build());

		if (unit.getAuthorizedAccesses().stream().anyMatch(access -> access.getEmail().equals(email))
				|| unit.getAuthor().getEmail().equals(email))
			return true;
		else
			throw new INDIeExceptionBuilder("Unit access not authorized").status(Status.UNAUTHORIZED)
					.errorFields(ErrorField.listOf(unitResource, "Not authorized")).build();
	}

	List<LTIUser> findUnitAccesesForUnit(String userId, int unitId) throws INDIeException {
		Unit unit = unitRepository.findByIdAndAuthorId(unitId, userId)
				.orElseThrow(() -> INDIeExceptionFactory.createUnitNotFound(unitId));

		List<LTIUser> listOfUsers = new ArrayList<>();

		for (AuthorizacionAccess access : unit.getAuthorizedAccesses()) {
			Optional<UserData> userQuery = this.userRepository.findByEmail(access.getEmail());
			listOfUsers.add(LTIUser.fromAuthorizationAccess(access, userQuery.isPresent() ? userQuery.get() : null));
		}

		return listOfUsers;

	}

	void putUnitAccesesForUnit(String userId, int unitId, List<String> emails) throws INDIeException {
		if (!isEmailsValid(emails))
			throw INDIeExceptionFactory.createWrongParamsException(ErrorField.listOf("emails", "Invalid emails"));

		Unit unit = unitRepository.findByIdAndAuthorId(unitId, userId)
				.orElseThrow(() -> INDIeExceptionFactory.createUnitNotFound(unitId));

		unit.getAuthorizedAccesses().clear();
		unitRepository.save(unit);

		for (String email : emails)
			unit.getAuthorizedAccesses().add(new AuthorizacionAccess(unit, email));

		unitRepository.save(unit);
	}

	private boolean isEmailsValid(List<String> emails) {
		if (emails == null)
			return false;
		return !(emails.stream().anyMatch(email -> !StringUtils.isEmailValid(email)));
	}
}

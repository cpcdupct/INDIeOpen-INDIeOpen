package es.upct.cpcd.indieopen.user;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;

class UserFinder {

	private final UserRepository userRepository;

	UserFinder(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	UserData findUserById(String id) throws INDIeException {
		return userRepository.findById(id).orElseThrow(() -> INDIeExceptionFactory.createUserNotFoundException(id));
	}

	Optional<UserData> findUserByIdOpt(String id) {
		return userRepository.findById(id);
	}

	UserData findUserByResetPasswordToken(String token) throws INDIeException {
		return userRepository.findByResetPasswordToken(token)
				.orElseThrow(() -> INDIeExceptionFactory.createInvalidToken(token));
	}

	UserDetails findUserByEmail(String username) throws INDIeException {
		return userRepository.findByEmail(username)
				.orElseThrow(() -> INDIeExceptionFactory.createUserNotFoundException(username));

	}

	Optional<UserData> findUserByEmailOpt(String email) {
		return userRepository.findByEmail(email);
	}

	public List<UserData> getListOfAvaialbleUsers() {
		return userRepository.findAll();
	}
}

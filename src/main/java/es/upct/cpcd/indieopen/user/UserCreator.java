package es.upct.cpcd.indieopen.user;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import com.cpcd.microservices.app.servicescommons.models.requests.RegistrarUsuarioRequest;

import es.upct.cpcd.indieopen.common.Language;
import es.upct.cpcd.indieopen.common.exceptions.ErrorCodes;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.services.cipher.CipherException;
import es.upct.cpcd.indieopen.services.cipher.CipherService;
import es.upct.cpcd.indieopen.services.mail.MailService;
import es.upct.cpcd.indieopen.services.mail.MailServiceException;
import es.upct.cpcd.indieopen.services.userinfo.UserInfo;
import es.upct.cpcd.indieopen.services.userinfo.UserInfoService;
import es.upct.cpcd.indieopen.user.beans.CreateUserBean;
import es.upct.cpcd.indieopen.user.beans.UserInfoBean;
import es.upct.cpcd.indieopen.user.domain.UserBuilder;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;
import es.upct.cpcd.indieopen.utils.DateUtils;
import es.upct.cpcd.indieopen.utils.PasswordUtil;
import lombok.extern.log4j.Log4j2;

@Log4j2
class UserCreator {
	private static final String TOKEN_SEPARATOR = "&";
	private static final long DAYS = 3;

	private final UserRepository userRepository;
	private final CipherService cipherService;
	private final MailService mailService;
	private final UserInfoService userInfoService;

	UserCreator(UserRepository userRepository, CipherService cipherService, MailService mailService,
			UserInfoService userInfoService) {
		this.userRepository = userRepository;
		this.cipherService = cipherService;
		this.mailService = mailService;
		this.userInfoService = userInfoService;
	}

	UserData create(UserData user) {
		userRepository.save(user);
		return user;
	}

	UserData createUser(CreateUserBean createUserBean) throws INDIeException {
		// Find the user
		Optional<UserInfo> userQuery = userInfoService.findByEmail(createUserBean.getEmail());
		if (userQuery.isPresent())
			throw INDIeExceptionFactory.createDuplicatedUserException(createUserBean.getEmail());

		// Check if password is safe
		if (!PasswordUtil.safePassword(createUserBean.getPassword()))
			throw INDIeExceptionFactory.createUnsafePasswordException();

		// Generate user in microservice
		UserInfo userInfo = userInfoService.createUser(createUserBean.getEmail(), createUserBean.getName(),
				createUserBean.getLastName(), createUserBean.getPassword());

		UserData user = UserBuilder.createBuilder()
				.userCredentials(userInfo.getId(), userInfo.getCompleteName(), userInfo.getEmail())
				.withInfo(createUserBean.getInstitution(), createUserBean.getCountry()).build();

		userRepository.save(user);

		return user;
	}

	void updateUserInfo(String userId, UserInfoBean infoBean) throws INDIeException {
		UserData user = userRepository.findById(userId)
				.orElseThrow(() -> INDIeExceptionFactory.createUserNotFoundException(userId));

		user.setCompleteName(infoBean.getName() + " " + infoBean.getSurname());
		user.setCountry(infoBean.getCountry());
		user.setInstitution(infoBean.getInstitution());
		user.setBiography(infoBean.getBiography());

		userInfoService.updateInfo(userId, infoBean.getName(), infoBean.getSurname(), infoBean.getAvatar());
	}

	UserData registerUser(String token, @Valid CreateUserBean createUserBean) throws INDIeException {
		if (!verifyNewAccountToken(token, createUserBean.getEmail()))
			throw new INDIeExceptionBuilder("Invalid token").code(ErrorCodes.WRONG_TOKEN).status(Status.USER_ERROR)
					.build();

		Optional<UserInfo> userQuery = userInfoService.findByEmail(createUserBean.getEmail());
		if (userQuery.isPresent())
			throw INDIeExceptionFactory.createDuplicatedUserException(createUserBean.getEmail());

		UserData user = this.createUser(createUserBean);

		try {
			this.mailService.sendEmailNewUser(createUserBean.getName(), createUserBean.getEmail(),
					Language.get(createUserBean.getLanguage()));
		} catch (MailServiceException e) {
			log.error("Welcome email has not been sent to user " + createUserBean.getEmail());
		}

		return user;
	}

	String generateNewAccountToken(String email) throws INDIeException {
		String plainToken = generatePlainToken(email, LocalDateTime.now().plusDays(DAYS));
		try {
			return cipherService.encrypt(plainToken);
		} catch (CipherException e) {
			throw INDIeExceptionFactory.createInternalException(e);
		}
	}

	private boolean verifyNewAccountToken(String encryptedToken, String email) {
		try {
			String plainToken = cipherService.decrypt(encryptedToken);
			String[] values = plainToken.split(TOKEN_SEPARATOR);
			String tokenEmail = values[0];
			String dateInString = values[1];

			if (!isOnTime(DateUtils.dateParseFromISOString(dateInString)))
				return false;

			return (email.equals(tokenEmail));
		} catch (CipherException e) {
			return false;
		}
	}

	private boolean isOnTime(LocalDateTime expirationDate) {
		return (DateUtils.before(LocalDateTime.now(), expirationDate));
	}

	private String generatePlainToken(String email, LocalDateTime dateTime) {
		return separateStrings(email, DateUtils.dateToISOString(dateTime), UUID.randomUUID().toString());
	}

	private String separateStrings(String... strings) {
		StringBuilder stBuilder = new StringBuilder();
		stBuilder.append(strings[0]);

		for (int i = 1; i < strings.length; i++)
			stBuilder.append(TOKEN_SEPARATOR).append(strings[i]);

		return stBuilder.toString();
	}

	public String getTokenAccountInfo(String encryptedToken) throws INDIeException {
		try {
			String plainToken = cipherService.decrypt(encryptedToken);
			String[] values = plainToken.split(TOKEN_SEPARATOR);
			String tokenEmail = values[0];
			String dateInString = values[1];

			if (!isOnTime(DateUtils.dateParseFromISOString(dateInString)))
				throw INDIeExceptionFactory.createInvalidToken(encryptedToken);

			return tokenEmail;
		} catch (CipherException e) {
			throw INDIeExceptionFactory.createInvalidToken(encryptedToken);
		}
	}

	public UserData createUser(RegistrarUsuarioRequest request) throws INDIeException {
		if (this.userRepository.findByEmail(request.getEmail()).isEmpty()) {
			UserData data = UserBuilder.createBuilder()
					.userCredentials(request.getId(), request.getCompleteName(), request.getEmail())
					.withInfo(request.getInstitution(), request.getCountry()).build();

			userRepository.save(data);
			return data;
		} else
			throw INDIeExceptionFactory.createDuplicatedUserException(request.getEmail());
	}

}

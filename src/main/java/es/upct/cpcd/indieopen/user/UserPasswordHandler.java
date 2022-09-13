package es.upct.cpcd.indieopen.user;

import es.upct.cpcd.indieopen.common.Language;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.services.mail.MailService;
import es.upct.cpcd.indieopen.services.mail.MailServiceException;
import es.upct.cpcd.indieopen.services.userinfo.UserInfo;
import es.upct.cpcd.indieopen.services.userinfo.UserInfoService;
import es.upct.cpcd.indieopen.user.beans.ChangePasswordBean;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;
import es.upct.cpcd.indieopen.utils.PasswordUtil;
import lombok.extern.log4j.Log4j2;

@Log4j2
class UserPasswordHandler {
	private final UserRepository userRepository;
	private UserInfoService userInfoService;
	private final UserTokenGenerator userTokenGenerator;
	private MailService mailService;

	UserPasswordHandler(UserRepository userRepository, MailService mailService, UserTokenGenerator userTokenGenerator,
			UserInfoService userInfoService) {
		this.userRepository = userRepository;
		this.userTokenGenerator = userTokenGenerator;
		this.mailService = mailService;
		this.userInfoService = userInfoService;
	}

	void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	String requestResetPassword(String username, Language language) throws INDIeException {
		try {
			UserInfo userInfo = userInfoService.findByEmail(username)
					.orElseThrow(() -> INDIeExceptionFactory.createUserNotFoundException(username));

			UserData user = userRepository.findById(userInfo.getId()).orElseThrow(IllegalStateException::new);

			String resetPasswordToken = userTokenGenerator.generateTokenResetPassword();
			user.setResetPasswordToken(resetPasswordToken);

			mailService.sendEmailPasswordRecovery(userInfo.getEmail(), userInfo.getCompleteName(), resetPasswordToken,
					language != null ? language : Language.ENGLISH);

			return resetPasswordToken;
		} catch (MailServiceException dae) {
			log.error(dae);
			throw INDIeExceptionFactory.createInternalException(dae);
		}

	}

	void updateUserPassword(String token, String password) throws INDIeException {
		if (!PasswordUtil.safePassword(password))
			throw INDIeExceptionFactory.createUnsafePasswordException();

		UserData userData = this.userRepository.findByResetPasswordToken(token)
				.orElseThrow(() -> INDIeExceptionFactory.createInvalidToken(token));

		userInfoService.updatePassword(userData, password);
		userData.clearResetPasswordToken();
		userRepository.save(userData);
	}

	public void changePassword(String userId, ChangePasswordBean passwordBean) throws INDIeException {
		UserData userData = userRepository.findById(userId)
				.orElseThrow(() -> INDIeExceptionFactory.createUserNotFoundException(userId));

		if (!PasswordUtil.safePassword(passwordBean.getNewPassword()))
			throw INDIeExceptionFactory.createUnsafePasswordException();

		userInfoService.updatePassword(userData, passwordBean.getNewPassword());
		userData.clearResetPasswordToken();
		userRepository.save(userData);
	}
}

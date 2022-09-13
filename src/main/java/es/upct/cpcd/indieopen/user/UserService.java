package es.upct.cpcd.indieopen.user;

import static es.upct.cpcd.indieopen.utils.LogUtils.log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.cpcd.microservices.app.servicescommons.models.requests.RegistrarUsuarioRequest;

import es.upct.cpcd.indieopen.common.Language;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.services.cipher.CipherService;
import es.upct.cpcd.indieopen.services.mail.MailService;
import es.upct.cpcd.indieopen.services.userinfo.UserInfoService;
import es.upct.cpcd.indieopen.unit.web.resources.AuthorResource;
import es.upct.cpcd.indieopen.user.beans.ChangePasswordBean;
import es.upct.cpcd.indieopen.user.beans.CreateUserBean;
import es.upct.cpcd.indieopen.user.beans.UserInfoBean;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;
import lombok.extern.log4j.Log4j2;

/**
 * Defines the available operations in the application for the User entity.
 *
 * @author mario
 */
@Service(value = "indieUserProvider")
@Transactional(rollbackFor = INDIeException.class)
@Log4j2
@Validated
public class UserService implements UserDetailsService {
	// Modules
	private final UserCreator userCreator;
	private final UserPasswordHandler userPasswordHandler;
	private final UserFinder userFinder;

	@Autowired
	public UserService(UserInfoService userInfoService, UserRepository userRepository, MailService mailService,
			CipherService cipherService) {
		this.userCreator = new UserCreator(userRepository, cipherService, mailService, userInfoService);
		this.userPasswordHandler = new UserPasswordHandler(userRepository, mailService, new UserTokenGenerator(),
				userInfoService);
		this.userFinder = new UserFinder(userRepository);
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userPasswordHandler.setUserInfoService(userInfoService);
	}

	public void setMailService(MailService mailService) {
		this.userPasswordHandler.setMailService(mailService);
	}

	public Optional<UserData> findUserById(String id) {
		return this.userFinder.findUserByIdOpt(id);
	}

	public UserData create(UserData user) throws INDIeException {
		return userCreator.create(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			return this.userFinder.findUserByEmail(username);
		} catch (INDIeException e) {
			throw new UsernameNotFoundException(username);
		}
	}

	public UserData registerUser(String token, @Valid CreateUserBean createUserBean) throws INDIeException {
		try {
			return userCreator.registerUser(token, createUserBean);
		} catch (INDIeException e) {
			log(log, e);
			throw e;
		}
	}

	public void requestResetPassword(String username, Language language) throws INDIeException {
		try {
			userPasswordHandler.requestResetPassword(username, language);
		} catch (INDIeException e) {
			log(log, e);
			throw e;
		}
	}

	public void updateUserPassword(String token, String newPassword) throws INDIeException {
		try {
			this.userPasswordHandler.updateUserPassword(token, newPassword);
		} catch (INDIeException e) {
			log(log, e);
			throw e;
		}
	}

	public void changePassword(String currentUserId, @Valid ChangePasswordBean passwordBean) throws INDIeException {
		try {
			this.userPasswordHandler.changePassword(currentUserId, passwordBean);
		} catch (INDIeException e) {
			log(log, e);
			throw e;
		}
	}

	public void updateUserPasswordToken(String token, String password) throws INDIeException {
		this.updateUserPassword(token, password);
	}

	public UserData findUserInfo(String id) throws INDIeException {
		return this.userFinder.findUserById(id);
	}

	public void updateUserInfo(String currentUserId, @Valid UserInfoBean infoBean) throws INDIeException {
		this.userCreator.updateUserInfo(currentUserId, infoBean);
	}

	public String generateNewAccountToken(String email) throws INDIeException {
		return this.userCreator.generateNewAccountToken(email);
	}

	public String getTokenAccountInfo(String token) throws INDIeException {
		return this.userCreator.getTokenAccountInfo(token);
	}

	public UserData findUserByResetTokenPassword(String token) throws INDIeException {
		return this.userFinder.findUserByResetPasswordToken(token);
	}

	public UserData createUser(RegistrarUsuarioRequest request) throws INDIeException {
		return this.userCreator.createUser(request);
	}

	public Optional<UserData> findUserByEmail(String email) {
		return this.userFinder.findUserByEmailOpt(email);
	}

	public List<AuthorResource> getListOfAvaialbleUsers() {
		return this.userFinder.getListOfAvaialbleUsers().stream().map(AuthorResource::from)
				.collect(Collectors.toList());
	}

}

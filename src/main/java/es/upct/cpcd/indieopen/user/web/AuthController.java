package es.upct.cpcd.indieopen.user.web;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.upct.cpcd.indieopen.common.Language;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.response.JSONResponse;
import es.upct.cpcd.indieopen.infraestructure.authenticate.JwtTokenProvider;
import es.upct.cpcd.indieopen.services.userinfo.UserInfo;
import es.upct.cpcd.indieopen.services.userinfo.UserInfoService;
import es.upct.cpcd.indieopen.user.UserService;
import es.upct.cpcd.indieopen.user.beans.CreateUserBean;
import es.upct.cpcd.indieopen.user.beans.NewPasswordBean;
import es.upct.cpcd.indieopen.user.beans.RequestResetPasswordBean;
import es.upct.cpcd.indieopen.user.domain.UserData;

@RestController
@RequestMapping("/auth")
class AuthController {

	final JwtTokenProvider jwtTokenProvider;
	final UserService userService;
	final UserInfoService userInfoService;

	@Autowired
	public AuthController(JwtTokenProvider jwtTokenProvider, UserService userService, UserInfoService userInfoService) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.userService = userService;
		this.userInfoService = userInfoService;
	}

	@GetMapping("/info/{token}")
	public ResponseEntity<?> tokenInfo(@PathVariable String token) throws INDIeException {
		UserData user = this.userService.findUserByResetTokenPassword(token);
		UserInfo info = userInfoService.findById(user.getId()).orElseThrow(IllegalStateException::new);
		return ResponseEntity.ok().body(JSONResponse.create().add("email", info.getEmail()).toBodyString());
	}

	@GetMapping("/infoAccount/{token}")
	public ResponseEntity<?> tokenAccountInfo(@PathVariable String token) throws INDIeException {
		String email = this.userService.getTokenAccountInfo(token);
		return ResponseEntity.ok().body(JSONResponse.create().add("email", email).toBodyString());
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<?> requestResetPassword(@RequestBody @Valid RequestResetPasswordBean requestResetPasswordBean)
			throws INDIeException {
		this.userService.requestResetPassword(requestResetPasswordBean.getEmail(),
				Language.get(requestResetPasswordBean.getLanguage()));
		return ResponseEntity.ok().build();
	}

	@PutMapping("/newPassword/{token}")
	public ResponseEntity<?> newPassword(@PathVariable String token,
			@RequestBody @Valid NewPasswordBean newPasswordBean) throws INDIeException {
		this.userService.updateUserPasswordToken(token, newPasswordBean.getPassword());
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/newAccount/{token}")
	public ResponseEntity<?> createNewAccount(@PathVariable String token, @RequestBody CreateUserBean createUserBean)
			throws INDIeException {
		UserData user = this.userService.registerUser(token, createUserBean);
		return ResponseEntity.created(URI.create("/api/user/info/" + user.getId())).build();
	}

}
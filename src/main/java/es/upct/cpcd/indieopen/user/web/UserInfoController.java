package es.upct.cpcd.indieopen.user.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.upct.cpcd.indieopen.common.BaseController;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.services.userinfo.UserInfoService;
import es.upct.cpcd.indieopen.unit.web.resources.AuthorResource;
import es.upct.cpcd.indieopen.user.UserService;
import es.upct.cpcd.indieopen.user.beans.ChangePasswordBean;
import es.upct.cpcd.indieopen.user.beans.UserInfoBean;
import es.upct.cpcd.indieopen.user.web.resources.UserInfoResource;

@RestController
@RequestMapping("/user")
class UserInfoController extends BaseController {
	private final UserService userService;
	private final UserInfoService userInfoService;

	@Autowired
	public UserInfoController(UserService userService, UserInfoService userInfoService) {
		this.userService = userService;
		this.userInfoService = userInfoService;
	}

	@GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserInfoResource getUserInfo() throws INDIeException {
		return UserInfoResource.fromUser(userService.findUserInfo(getCurrentUserId()),
				this.userInfoService.findById(getCurrentUserId()).get());
	}

	@PutMapping(value = "/info")
	public ResponseEntity<?> updateUserInfo(@RequestBody UserInfoBean infoBean) throws INDIeException {
		userService.updateUserInfo(getCurrentUserId(), infoBean);
		return ResponseEntity.ok().build();
	}

	@PutMapping(value = "/password")
	public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordBean passwordBean) throws INDIeException {
		userService.changePassword(getCurrentUserId(), passwordBean);
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/available", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<AuthorResource> findTopAuthors() throws INDIeException {
		return userService.getListOfAvaialbleUsers();
	}
}

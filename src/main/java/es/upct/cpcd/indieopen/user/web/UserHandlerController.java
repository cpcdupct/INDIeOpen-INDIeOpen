package es.upct.cpcd.indieopen.user.web;

import java.net.URISyntaxException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpcd.microservices.app.servicescommons.models.entity.UsuarioQuery;
import com.cpcd.microservices.app.servicescommons.models.requests.RegistrarUsuarioRequest;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.user.UserService;
import es.upct.cpcd.indieopen.user.domain.UserData;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/userhandle")
@Log4j2
public class UserHandlerController {

	private UserService userService;

	@Autowired
	public UserHandlerController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/users")
	public UsuarioQuery createUser(@RequestBody RegistrarUsuarioRequest request)
			throws INDIeException, URISyntaxException {
		UserData userData = userService.createUser(request);
		return new UsuarioQuery(userData.getEmail(), userData.getCompleteName());
	}

	@GetMapping("/users/byEmail/{email}")
	public ResponseEntity<UsuarioQuery> getUserByEmail(@PathVariable String email) {
		log.info("request");
		log.info(email);

		Optional<UserData> userQuery = this.userService.findUserByEmail(email);

		if (userQuery.isEmpty())
			return ResponseEntity.notFound().build();
		else
			return ResponseEntity.ok(new UsuarioQuery(userQuery.get().getEmail(), userQuery.get().getCompleteName()));
	}
}

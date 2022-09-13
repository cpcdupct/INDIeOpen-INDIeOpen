package es.upct.cpcd.indieopen.services.userinfo;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpcd.microservices.app.servicescommons.models.entity.Usuario;
import com.cpcd.microservices.app.servicescommons.models.requests.ActualizarInfoUsuarioRequest;
import com.cpcd.microservices.app.servicescommons.models.requests.NuevoUsuarioRequest;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.user.domain.UserData;
import feign.FeignException;

@Service
public class UserInfoService {

	private final UsuarioFeignClient usuarioFeignClient;

	@Autowired
	public UserInfoService(UsuarioFeignClient usuarioFeignClient) {
		this.usuarioFeignClient = usuarioFeignClient;
	}

	public Optional<UserInfo> findByEmail(String email) {
		try {
			return Optional.of(UserInfo.from(usuarioFeignClient.findByEmail(email)));
		} catch (FeignException fe) {
			return Optional.empty();
		}
	}

	public void updatePassword(UserData userData, String password) throws INDIeException {
		try {
			usuarioFeignClient.updatePassword(userData.getId(), password);
		} catch (FeignException fe) {
			throw new INDIeException("Error actualizando password en", fe);
		}
	}

	public Optional<UserInfo> findById(String userId) {
		try {
			return Optional.of(UserInfo.from(usuarioFeignClient.findById(userId)));
		} catch (FeignException fe) {
			return Optional.empty();
		}
	}

	public UserInfo createUser(String email, String name, String lastName, String password) throws INDIeException {
		NuevoUsuarioRequest nuevoUsuarioRequest = new NuevoUsuarioRequest(email, name, lastName, password, "INDIe");

		try {
			Usuario usuario = usuarioFeignClient.addUsuario(nuevoUsuarioRequest);
			return UserInfo.from(usuario);
		} catch (FeignException fe) {
			throw new INDIeException("Error creando usuario en UserInfoService - createUser", fe);
		}
	}

	public void updateInfo(String id, String nombre, String apellidos, String avatar) throws INDIeException {
		ActualizarInfoUsuarioRequest request = new ActualizarInfoUsuarioRequest(nombre, apellidos, avatar);

		try {
			usuarioFeignClient.updateUserInfo(request, id);
		} catch (FeignException fe) {
			throw new INDIeException("Error creando en updateInfo en UserInfoService - createUser", fe);
		}
	}
}

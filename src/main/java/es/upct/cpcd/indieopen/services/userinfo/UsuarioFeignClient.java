package es.upct.cpcd.indieopen.services.userinfo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cpcd.microservices.app.servicescommons.models.entity.Usuario;
import com.cpcd.microservices.app.servicescommons.models.requests.ActualizarInfoUsuarioRequest;
import com.cpcd.microservices.app.servicescommons.models.requests.NuevoUsuarioRequest;

@FeignClient(name="${feign.microserviciousuarios}")
public interface UsuarioFeignClient {

	@GetMapping("/email/{email}")
	Usuario findByEmail(@PathVariable String email);

	@GetMapping("/search/{id}")
	Usuario findById(@PathVariable String id);

	@PostMapping("/")
	Usuario addUsuario(@RequestBody NuevoUsuarioRequest nuevoUsuarioRequest);

	@PutMapping("/info/{id}")
	void updateUserInfo(@RequestBody ActualizarInfoUsuarioRequest request, @PathVariable String id);

	@PutMapping("/password/{id}/{password}")
	void updatePassword(@PathVariable String id, @PathVariable String password);

}

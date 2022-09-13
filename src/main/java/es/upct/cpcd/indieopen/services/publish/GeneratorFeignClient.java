package es.upct.cpcd.indieopen.services.publish;

import com.cpcd.microservices.app.servicescommons.models.requests.LearningUnitRequest;
import com.cpcd.microservices.app.servicescommons.models.requests.LearningUnitRequest.UnitTypes;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="${feign.microserviciogenerator}")
public interface GeneratorFeignClient {

	@PostMapping("/contentgenerator")
	void generateContentUnit(@RequestBody LearningUnitRequest request);

	@PostMapping("/evaluationgenerator")
	void generateEvaluationUnit(@RequestBody LearningUnitRequest request);

	@DeleteMapping("/learninggenerator/{unitid}/{teacherid}/{unitTypes}")
	void deleteUnit(@PathVariable String unitid, @PathVariable String teacherid, @PathVariable UnitTypes unitTypes);

}

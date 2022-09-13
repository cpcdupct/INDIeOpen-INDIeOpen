package es.upct.cpcd.indieopen.unit.web;

import static org.springframework.http.ResponseEntity.ok;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.upct.cpcd.indieopen.common.BaseController;
import es.upct.cpcd.indieopen.common.Language;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.resources.LTIUser;
import es.upct.cpcd.indieopen.unit.UnitService;
import es.upct.cpcd.indieopen.unit.beans.ChangeLicenseBean;
import es.upct.cpcd.indieopen.unit.beans.CreateUnitBean;
import es.upct.cpcd.indieopen.unit.beans.UpdateUnitInfoBean;
import es.upct.cpcd.indieopen.unit.domain.CreativeCommons;
import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.web.resources.OriginalUnitStatusResource;
import es.upct.cpcd.indieopen.unit.web.resources.TokenResource;
import es.upct.cpcd.indieopen.unit.web.resources.URLResource;
import es.upct.cpcd.indieopen.unit.web.resources.UnitResource;

@RestController
@RequestMapping("/units")
public class UnitController extends BaseController {

	private final UnitService unitsService;

	@Autowired
	public UnitController(UnitService unitsService) {
		this.unitsService = unitsService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Page<UnitResource> findUnitsByUser(@PageableDefault(sort = "name", direction = Direction.ASC) Pageable page,
			@RequestParam(name = "type", required = false) String type) {
		return unitsService.findUnitsByUser(getCurrentUserId(), page, type);
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UnitResource> createUnit(@RequestBody CreateUnitBean createUnitBean) throws INDIeException {
		Unit unit = unitsService.createUnit(getCurrentUserId(), createUnitBean);
		return ResponseEntity.created(URI.create("/api/units/" + unit.getId())).body(UnitResource.fromUnit(unit));
	}

	@DeleteMapping(value = "/{unitId}")
	public ResponseEntity<?> deleteUnit(@PathVariable int unitId) throws INDIeException {
		unitsService.deleteUnit(getCurrentUserId(), unitId);
		return ok().build();
	}

	@PutMapping(value = "/{unitId}/info")
	public ResponseEntity<UnitResource> updateUnit(@PathVariable int unitId,
			@RequestBody UpdateUnitInfoBean updateUnitBean) throws INDIeException {
		UnitResource unit = unitsService.updateUnit(getCurrentUserId(), unitId, updateUnitBean);
		return ResponseEntity.ok().body(unit);
	}

	@GetMapping(value = "/{unitId}/detail", produces = MediaType.APPLICATION_JSON_VALUE)
	public UnitResource findUnitById(@PathVariable int unitId) throws INDIeException {
		return unitsService.findUnitResourceByUserAndId(getCurrentUserId(), unitId);
	}

	@GetMapping(value = "/{unitId}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
	public TokenResource generateEditToken(@PathVariable int unitId) throws INDIeException {
		String token = unitsService.generateToken(getCurrentUserId(), unitId);
		return TokenResource.from(token);
	}

	@PostMapping(value = "/{unitId}/preview")
	public ResponseEntity<URLResource> previewModel(@PathVariable int unitId)
			throws INDIeException, URISyntaxException {
		String previewUrl = unitsService.previewUnit(getCurrentUserId(), unitId);
		return ResponseEntity.created(new URI(previewUrl)).body(new URLResource(previewUrl));
	}

	@PostMapping(value = "/{unitId}/publish")
	public ResponseEntity<URLResource> publishUnit(@PathVariable int unitId) throws INDIeException, URISyntaxException {
		String publishUrl = unitsService.publishUnit(getCurrentUserId(), unitId);
		return ResponseEntity.created(new URI(publishUrl)).body(new URLResource(publishUrl));
	}

	@PostMapping(value = "/{unitId}/add", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UnitResource> addSharedUnit(@PathVariable int unitId,
			@RequestParam(name = "language") String language) throws INDIeException {
		Unit unit = unitsService.addSharedUnit(getCurrentUserId(), unitId, Language.get(language));
		return ResponseEntity.created(URI.create("/api/units/" + unit.getId())).body(UnitResource.fromUnit(unit));
	}

	@PutMapping(value = "/{unitId}/license", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> changeLicense(@PathVariable int unitId, @RequestBody @Valid ChangeLicenseBean bean)
			throws INDIeException {
		unitsService.changeLicense(getCurrentUserId(), unitId, CreativeCommons.get(bean.getLicense()));
		return ok().build();
	}

	@GetMapping(value = "/{unitId}/original", produces = MediaType.APPLICATION_JSON_VALUE)
	public OriginalUnitStatusResource getOriginalUnitStatus(@PathVariable int unitId) throws INDIeException {
		return unitsService.getOriginalUnitStatus(getCurrentUserId(), unitId);
	}

	@PutMapping(value = "/{unitId}/update")
	public ResponseEntity<?> updateVersion(@PathVariable int unitId) throws INDIeException {
		this.unitsService.updateVersionUnit(getCurrentUserId(), unitId);
		return ok().build();
	}

	@PutMapping(value = "/{unitId}/analytics")
	public ResponseEntity<?> updateVersion(@PathVariable int unitId,
			@RequestParam(name = "analytics") boolean analytics) throws INDIeException {
		this.unitsService.toggleLearningAnalytics(getCurrentUserId(), unitId, analytics);
		return ok().build();
	}

	// ACCESS
	@GetMapping(value = "/{unitId}/access", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<LTIUser> findAccessesForUnit(@PathVariable int unitId) throws INDIeException {
		return this.unitsService.findUnitAccesesForUnit(getCurrentUserId(), unitId);
	}

	@PutMapping(value = "/{unitId}/access")
	public ResponseEntity<?> putUnitAccess(@PathVariable int unitId, @RequestBody List<String> emails)
			throws INDIeException {
		this.unitsService.putUnitAccesesForUnit(getCurrentUserId(), unitId, emails);
		return ok().build();
	}

}

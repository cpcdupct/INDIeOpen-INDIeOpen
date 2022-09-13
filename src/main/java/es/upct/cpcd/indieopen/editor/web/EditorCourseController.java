package es.upct.cpcd.indieopen.editor.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.upct.cpcd.indieopen.common.BaseController;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.editor.EditorService;
import es.upct.cpcd.indieopen.editor.web.resources.EditorUnitResource;
import es.upct.cpcd.indieopen.token.ModelToken;
import es.upct.cpcd.indieopen.unit.UnitService;

@RestController
@RequestMapping("/editor/course")
public class EditorCourseController extends BaseController {

	private final EditorService editorService;
	private final UnitService unitService;

	@Autowired
	EditorCourseController(EditorService editorService, UnitService unitService) {
		this.editorService = editorService;
		this.unitService = unitService;
	}

	@GetMapping(value = "/units/{token}")
	public List<EditorUnitResource> getUnits(@PathVariable("token") String token) throws INDIeException {
		ModelToken tokenInfo = this.editorService.getTokenInfo(token);
		return this.unitService.findAllPublishedUnits(tokenInfo.getUser()).stream().map(EditorUnitResource::fromUnit)
				.collect(Collectors.toList());
	}

}

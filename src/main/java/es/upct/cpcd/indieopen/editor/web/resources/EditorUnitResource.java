package es.upct.cpcd.indieopen.editor.web.resources;

import es.upct.cpcd.indieopen.unit.domain.Unit;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class EditorUnitResource {
	private String name;
	private String id;
	private String description;
	private String type;
	private String cover;
	private String link;

	public static EditorUnitResource fromUnit(Unit u) {
		EditorUnitResource resource = new EditorUnitResource();

		resource.name = u.getName();
		resource.id = u.getUnitResourceId();
		resource.description = u.getShortDescription();
		resource.cover = u.getCover();
		resource.type= u.getUnitType().getValue();
		resource.link = u.getResource();

		return resource;
	}

}

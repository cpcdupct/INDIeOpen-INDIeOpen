package es.upct.cpcd.indieopen.unit.web.resources;

import es.upct.cpcd.indieopen.educationalcontext.domain.EducationalContext;
import es.upct.cpcd.indieopen.unit.domain.Unit;
import lombok.Getter;

@Getter
public class UnitInformationResource {

	private int category;
	private String shortDescription;
	private String longDescription;
	private String cover;
	private String[] tags;
	private String name;
	private String language;
	private boolean draft;
	private Integer[] ageRange;
	private String theme;

	private String[] educationalContext;

	public static UnitInformationResource from(Unit unit) {
		UnitInformationResource resource = new UnitInformationResource();
		resource.category = unit.getCategory().getId();
		resource.name = unit.getName();
		resource.shortDescription = unit.getShortDescription();
		resource.longDescription = unit.getLongDescription();
		resource.language = unit.getLanguage().getValue();
		resource.cover = unit.getCover();
		resource.tags = unit.getTags();
		resource.draft = unit.isDraft();
		resource.theme = unit.getTheme();
		resource.ageRange = unit.getAgeRange().toRangeArray();
		resource.educationalContext = unit.getEducationalContexts().stream()
				.map(EducationalContext::getEducationalLevel).toArray(String[]::new);

		return resource;
	}

}

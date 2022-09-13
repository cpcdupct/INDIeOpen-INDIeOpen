package es.upct.cpcd.indieopen.unit.web.resources;

import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UnitResource {

	private int id;
	private String createdAt;
	private String updatedAt;
	private String type;
	private String mode;
	private String license;
	private String creativeCommons;
	private AuthorResource author;
	private UnitInformationResource information;
	private RatingResource rating;
	private PublishedResource published;
	private OriginalUnitStampResource originalUnit;

	public static UnitResource fromUnit(Unit unit) {
		UnitResource resource = new UnitResource();

		resource.id = unit.getId();
		resource.information = UnitInformationResource.from(unit);
		resource.createdAt = DateUtils.dateToISOString(unit.getCreatedAt());
		resource.updatedAt = DateUtils.dateToISOString(unit.getUpdatedAt());
		resource.creativeCommons = unit.getCreativeCommons().getValue();
		resource.license = unit.getLicense().getValue();
		resource.mode = unit.getMode().getValue();
		resource.author = AuthorResource.from(unit.getAuthor());
		resource.rating = RatingResource.from(unit.getRatingAverage(), unit.getRatingCount());
		resource.type = unit.getUnitType().toString();
		resource.published = PublishedResource.from(unit);

		resource.originalUnit = OriginalUnitStampResource.from(unit);

		return resource;
	}
}

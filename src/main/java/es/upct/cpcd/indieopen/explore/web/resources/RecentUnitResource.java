package es.upct.cpcd.indieopen.explore.web.resources;

import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.web.resources.AuthorResource;
import es.upct.cpcd.indieopen.unit.web.resources.RatingResource;
import lombok.Getter;

@Getter
public class RecentUnitResource {
	private int id;
	private String name;
	private String cover;
	private int category;
	private String type;
	private RatingResource rating;
	private AuthorResource author;

	public static RecentUnitResource from(Unit unit) {
		RecentUnitResource resource = new RecentUnitResource();

		resource.id = unit.getId();
		resource.name = unit.getName();
		resource.cover = unit.getCover();
		resource.type = unit.getUnitType().getValue();
		resource.category = unit.getCategory().getId();
		resource.rating = RatingResource.from(unit.getRatingAverage(), unit.getRatingCount());
		resource.author = AuthorResource.from(unit.getAuthor());

		return resource;
	}
}

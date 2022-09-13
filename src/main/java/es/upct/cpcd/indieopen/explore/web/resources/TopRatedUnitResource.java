package es.upct.cpcd.indieopen.explore.web.resources;

import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.web.resources.RatingResource;
import es.upct.cpcd.indieopen.utils.DateUtils;
import lombok.Getter;

@Getter
public class TopRatedUnitResource {
	private int id;
	private String name;
	private String cover;
	private String publishedAt;
	private RatingResource rating;
	private String description;

	public static TopRatedUnitResource from(Unit unit) {
		TopRatedUnitResource resource = new TopRatedUnitResource();

		resource.id = unit.getId();
		resource.name = unit.getName();
		resource.cover = unit.getCover();
		resource.publishedAt = DateUtils.dateToISOString(unit.getPublishedDate());
		resource.rating = RatingResource.from(unit.getRatingAverage(), unit.getRatingCount());
		resource.description = unit.getShortDescription();

		return resource;
	}
}

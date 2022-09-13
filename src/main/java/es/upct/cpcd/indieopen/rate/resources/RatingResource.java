package es.upct.cpcd.indieopen.rate.resources;

import es.upct.cpcd.indieopen.rate.domain.Rate;
import es.upct.cpcd.indieopen.unit.web.resources.AuthorResource;
import es.upct.cpcd.indieopen.unit.web.resources.UnitInformationResource;
import lombok.Getter;

@Getter
public class RatingResource {

    private Integer id;
    private int rating;
    private AuthorResource author;
    private UnitInformationResource unit;

    public static RatingResource from(Rate rate) {
        RatingResource resource = new RatingResource();
        resource.id = rate.getId();
        resource.rating = rate.getRating();
        resource.author = AuthorResource.from(rate.getAuthor());
        resource.unit = UnitInformationResource.from(rate.getUnit());

        return resource;
    }
}

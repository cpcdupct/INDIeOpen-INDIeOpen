package es.upct.cpcd.indieopen.unit.web.resources;

import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.utils.DateUtils;
import lombok.Getter;

@Getter
public class PublishedResource {

    private String publishedDate;
    private String resource;
	private boolean analytics;


    public static PublishedResource from(Unit unit) {
        PublishedResource resource = new PublishedResource();
        resource.publishedDate = unit.getPublishedDate() != null ? DateUtils.dateToISOString(unit.getPublishedDate())
                : null;

        resource.resource = unit.getResource();
		resource.analytics = unit.isAnalytics();

        return resource;
    }

}

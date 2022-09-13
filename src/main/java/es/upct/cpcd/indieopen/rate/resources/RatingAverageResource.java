package es.upct.cpcd.indieopen.rate.resources;

import es.upct.cpcd.indieopen.unit.domain.Unit;
import lombok.Getter;

@Getter
public class RatingAverageResource {

    private int count;
    private double average;

    public static RatingAverageResource from(Unit u) {
        RatingAverageResource resource = new RatingAverageResource();

        resource.count = u.getRatingCount();
        resource.average = u.getRatingAverage();

        return resource;
    }
}

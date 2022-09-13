package es.upct.cpcd.indieopen.unit.web.resources;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RatingResource {

	private int count;
	private double average;

	public static RatingResource from(double ratingAverage, int ratingCount) {
		RatingResource resource = new RatingResource();

		resource.average = ratingAverage;
		resource.count = ratingCount;

		return resource;
	}
}

package es.upct.cpcd.indieopen.explore.web.resources;

import es.upct.cpcd.indieopen.user.domain.UserData;
import lombok.Getter;

@Getter
public class TopAuthorResource {
	private String photo;
	private String name;
	private String institution;
	private int units;
	private String bio;

	public static TopAuthorResource from(UserData user, int units) {
		TopAuthorResource resource = new TopAuthorResource();
		resource.name = user.getCompleteName();
		resource.units = units;
		resource.institution = user.getInstitution();
		resource.bio = user.getBiography();
		return resource;
	}

}

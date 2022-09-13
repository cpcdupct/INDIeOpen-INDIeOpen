package es.upct.cpcd.indieopen.unit.web.resources;

import es.upct.cpcd.indieopen.user.domain.UserData;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthorResource {
	private String id;
	private String email;
	private String name;

	public static AuthorResource from(UserData author) {
		AuthorResource resource = new AuthorResource();

		resource.id = author.getId();
		resource.email = author.getEmail();
		resource.name = author.getCompleteName();

		return resource;
	}
}

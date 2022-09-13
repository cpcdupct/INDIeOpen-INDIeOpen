package es.upct.cpcd.indieopen.user.web.resources;

import es.upct.cpcd.indieopen.services.userinfo.UserInfo;
import es.upct.cpcd.indieopen.user.domain.UserData;
import lombok.Getter;

@Getter
public class UserInfoResource {

	private String name;
	private String surname;
	private String country;
	private String institution;
	private String avatar;
	private String biography;

	public static UserInfoResource fromUser(UserData user, UserInfo userInfo) {
		UserInfoResource resource = new UserInfoResource();

		resource.name = userInfo.getNombre();
		resource.surname = userInfo.getApellidos();
		resource.country = user.getCountry();
		resource.institution = user.getInstitution();
		resource.avatar = userInfo.getAvatar();
		resource.biography = user.getBiography();

		return resource;
	}
}

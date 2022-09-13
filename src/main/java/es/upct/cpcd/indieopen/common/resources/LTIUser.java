package es.upct.cpcd.indieopen.common.resources;

import es.upct.cpcd.indieopen.course.domain.CourseAccess;
import es.upct.cpcd.indieopen.unit.domain.AuthorizacionAccess;
import es.upct.cpcd.indieopen.user.domain.UserData;
import lombok.Getter;

@Getter
public class LTIUser {

	private String email;
	private String name;

	private LTIUser(String email, String name) {
		this.email = email;
		this.name = name;
	}

	public static LTIUser fromAuthorizationAccess(AuthorizacionAccess access, UserData user) {
		return new LTIUser(access.getEmail(), user != null ? user.getCompleteName() : "");
	}
	
	public static LTIUser fromCourseAccess(CourseAccess access, UserData user) {
		return new LTIUser(access.getEmail(), user != null ? user.getCompleteName() : "");
	}

}

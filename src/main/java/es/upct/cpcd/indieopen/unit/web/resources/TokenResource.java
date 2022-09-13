package es.upct.cpcd.indieopen.unit.web.resources;

import lombok.Getter;

@Getter
public class TokenResource {

	public String token;

	private TokenResource(String token) {
		this.token = token;
	}

	public static TokenResource from(String token) {
		return new TokenResource(token);
	}

}

package es.upct.cpcd.indieopen.infraestructure.authenticate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
	private final UserDetailsService userDetailsService;

	@Autowired
	public JwtTokenProvider(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public Authentication getAuthentication(String token) {
		String[] parts = token.split("\\.", 0);
		String decoded = new String(Base64.getUrlDecoder().decode(parts[1]));
		JSONObject jsonObject = new JSONObject(decoded);
		String id = jsonObject.getString("id");
		UserDetails user = this.userDetailsService.loadUserByUsername(jsonObject.getString("correo"));
		List<SimpleGrantedAuthority> authorities = getAuthoritiesFromToken(jsonObject.getJSONArray("authorities"));
		return new UserAuthentication(id, user, "", authorities);
	}

	private List<SimpleGrantedAuthority> getAuthoritiesFromToken(JSONArray jsonArray) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			String authorityString = jsonArray.getString(i);
			authorities.add(new SimpleGrantedAuthority(authorityString));
		}

		return authorities;
	}

	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
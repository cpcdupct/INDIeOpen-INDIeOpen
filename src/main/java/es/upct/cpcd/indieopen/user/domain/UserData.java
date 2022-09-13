package es.upct.cpcd.indieopen.user.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import es.upct.cpcd.indieopen.utils.ModelUtils;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserData implements Serializable, UserDetails {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 40)
	private String id;

	@Column(nullable = false)
	private String completeName;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false, unique = true, length = 32)
	private String base;

	@Column(nullable = false, length = 150)
	private String institution;

	@Column(nullable = false, length = 90)
	private String country;

	@Column(length = 300)
	private String biography;

	@Column()
	private String resetPasswordToken;

	public UserData() {
		this.base = ModelUtils.randomUUID(true);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof UserData)) {
			return false;
		}
		UserData user = (UserData) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		return authorities;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	public void clearResetPasswordToken() {
		this.resetPasswordToken = null;
	}

	@Override
	public String toString() {
		return "UserData [id=" + id + ", completeName=" + completeName + ", email=" + email + ", base=" + base
				+ ", institution=" + institution + ", country=" + country + "]";
	}

}

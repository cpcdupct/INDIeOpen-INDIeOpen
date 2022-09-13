package es.upct.cpcd.indieopen.infraestructure.authenticate;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

public class UserAuthentication extends UsernamePasswordAuthenticationToken {
    private static final long serialVersionUID = 1L;

    private final String userId;

    public UserAuthentication(String userId, Object principal, Object credentials) {
        super(principal, credentials);
        this.userId = userId;
    }

    public UserAuthentication(String userId, Object principal, Object credentials, Collection<?
            extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UserAuthentication)) {
            return false;
        }
        UserAuthentication userAuthentication = (UserAuthentication) o;
        return userId.equals(userAuthentication.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }

}
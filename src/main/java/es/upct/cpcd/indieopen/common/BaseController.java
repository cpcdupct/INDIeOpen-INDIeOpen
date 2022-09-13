package es.upct.cpcd.indieopen.common;

import org.springframework.security.core.context.SecurityContextHolder;

import es.upct.cpcd.indieopen.infraestructure.authenticate.UserAuthentication;

/**
 * Common functions for every controller in web service controllers
 */
public abstract class BaseController {

    /**
     * Gets the current user id.
     * 
     * @throws IllegalStateException If the user is not logged in
     * 
     * @return userId
     */
    protected String getCurrentUserId() {
        try {
            UserAuthentication authentication = (UserAuthentication) SecurityContextHolder.getContext()
                    .getAuthentication();
            return authentication.getUserId();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
package es.upct.cpcd.indieopen.common.exceptions;

public class ErrorCodes {

	private ErrorCodes() {

	}

	/** User introduced bad credentials */
	public static final String USER_BAD_CREDENTIALS = "USER_BAD_CREDENTIALS";

	/** The user has sent a wrong password */
	public static final String WRONG_PASSWORD = "WRONG_PASSWORD";

	/** Error in validating the request */
	public static final String WRONG_PARAMS = "WRONG_PARAMS";

	/** The provided password is unsafe */
	public static final String UNSAFE_PASSWORD = "UNSAFE_PASSWORD";

	/**
	 * The request is done over an enitty which is not accesible by the user
	 * (nonExisting or not allowed)
	 */
	public static final String ENTITY_NOT_ACCESSIBLE = "ENTITY_NOT_ACCESSIBLE";

	/** Internal error in server */
	public static final String INTERNAL_ERROR = "INTERNAL_ERROR";

	/** No content in the document database has been found */
	public static final String NO_CONTENT_FOUND = "NO_CONTENT_FOUND";

	/** Editor token is not valid */
	public static final String EDITOR_TOKEN_NOT_VALID = "EDITOR_TOKEN_NOT_VALID";

	/** Unit is already published */
	public static final String UNIT_ALREADY_PUBLISHED = "UNIT_ALREADY_PUBLISHED";

	/** Unit is not shareable because it is not a concrete unit */
	public static final String UNIT_NOT_SHAREABLE = "UNIT_NOT_SHAREABLE";

	/** Unauthorized request */
	public static final String UNAUTHORIZED_REQUEST = "UNAUTHORIZED_REQUEST";

	/** The question is already used in a unit */
	public static final String USED_QUESTION = "USED_QUESTION";

	/** The token sent is not valid */
	public static final String WRONG_TOKEN = "WRONG_TOKEN";

	/** The unit is already added into user's units */
	public static final String UNIT_ALREADY_ADDED = "UNIT_ALREADY_ADDED";

	/** User already exists */
	public static final String EXISTING_USER = "EXISTING_USER";
	
	/** The original unit does not exist */
	public static final String ORIGINAL_UNIT_DOES_NOT_EXISTS = "ORIGINAL_UNIT_DOES_NOT_EXISTS";

}
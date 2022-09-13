package es.upct.cpcd.indieopen.utils;

import java.util.Base64;
import java.util.UUID;

public class StringUtils {
	private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
	public static final String EMPTY_STRING = "";
	public static final String URL_REGEX = "<\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]>";

	private StringUtils() {

	}

	public static boolean isNullOrEmpty(String string) {
		if (string == null)
			return true;

		return (string.length() == 0);
	}

	public static String base64Decode(String base64Content) {
		return new String(Base64.getDecoder().decode(base64Content));
	}

	public static String base64Encode(String base64Content) {
		return new String(Base64.getEncoder().encode(base64Content.getBytes()));
	}

	public static String randomUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static boolean isStringValid(String string) {
		return (string != null && (string.length() > 0));
	}

	public static boolean areStringValid(String... strings) {
		for (String string : strings)
			if (!isStringValid(string))
				return false;

		return true;
	}

	public static boolean isEmailValid(String email) {
		if (isNullOrEmpty(email))
			return false;

		return email.matches(EMAIL_REGEX);
	}

}

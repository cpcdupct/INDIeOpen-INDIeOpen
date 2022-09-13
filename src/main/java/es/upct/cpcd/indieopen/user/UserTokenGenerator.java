package es.upct.cpcd.indieopen.user;

class UserTokenGenerator {
	private static final int TOKEN_RESET_PASSWORD_LENGTH = 64;

	private static final char[] ELEMENTS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'R', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
			'V', 'W', 'X', 'Y', 'Z' };

	UserTokenGenerator() {

	}

	String generateTokenResetPassword() {
		return generate();
	}

	private String generate() {
		char[] set = new char[UserTokenGenerator.TOKEN_RESET_PASSWORD_LENGTH];
		for (int i = 0; i < UserTokenGenerator.TOKEN_RESET_PASSWORD_LENGTH; i++) {
			int el = (int) (Math.random() * ELEMENTS.length);
			set[i] = ELEMENTS[el];
		}

		return new String(set);
	}
}

package es.upct.cpcd.indieopen.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordGenerator;
import org.passay.PasswordValidator;
import org.passay.RuleResult;

public class PasswordUtil {
	private static final int MIN_LENGTH = 8;
	private static final int MAX_LENGTH = 24;
	private static final Random random = new Random();

	private static final PasswordValidator validator;
	private static final PasswordGenerator generator;
	private static final List<CharacterRule> characterRules;

	private PasswordUtil() {

	}

	static {
		validator = new PasswordValidator(new LengthRule(MIN_LENGTH, MAX_LENGTH),
				new CharacterRule(EnglishCharacterData.UpperCase, 1), new CharacterRule(EnglishCharacterData.Digit, 1));
		generator = new PasswordGenerator();
		characterRules = new ArrayList<>();
		characterRules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1));
		characterRules.add(new CharacterRule(EnglishCharacterData.Digit, 1));
	}

	public static boolean safePassword(String plainPassword) {
		if (StringUtils.isNullOrEmpty(plainPassword))
			return false;

		RuleResult result = validator.validate(new PasswordData(plainPassword));
		return (result.isValid());
	}

	public static String generateSafePassword() {
		return generator.generatePassword(randomPasswordLength(), characterRules);
	}

	private static int randomPasswordLength() {
		return random.nextInt((MAX_LENGTH - MIN_LENGTH) + 1) + MIN_LENGTH;
	}

}

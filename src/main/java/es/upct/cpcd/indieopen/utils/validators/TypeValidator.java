package es.upct.cpcd.indieopen.utils.validators;

import static es.upct.cpcd.indieopen.utils.StringUtils.isStringValid;

import es.upct.cpcd.indieopen.questions.beans.QuestionType;
import es.upct.cpcd.indieopen.unit.domain.License;
import es.upct.cpcd.indieopen.unit.domain.UnitType;

public class TypeValidator {

	private TypeValidator() {

	}

	public static boolean isLicenseValid(String usemode) {
		return isStringValid(usemode) && (License.ALLOW_READ_ONLY.getValue().equals(usemode)
				|| License.ALLOW_REUSE.getValue().equals(usemode));
	}

	public static boolean isTypeValid(String type) {
		return UnitType.get(type) != null;
	}

	public static boolean areCategoriesValid(String categories) {
		return (isStringValid(categories));
	}

	public static boolean isGroupValid(String groupKey) {
		if (!isStringValid(groupKey))
			return false;

		return (!"ALL".equals(groupKey));
	}

	public static boolean isQuestionTypeValid(String questionType) {
		return isStringValid(questionType) && (QuestionType.isType(questionType));
	}

	public static boolean isEducationalContextValid(String context) {
		if (!isStringValid(context))
			return false;

		return !("0".equals(context));
	}

	public static boolean isAgeRangeValid(String ageRange) {
		if (!isStringValid(ageRange))
			return false;

		return ageRange.split(",").length == 2;
	}

	public static boolean isLanguageValid(String language) {
		if (!isStringValid(language))
			return false;

		return !("all".equals(language));
	}

}
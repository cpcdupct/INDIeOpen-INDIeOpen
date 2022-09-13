package es.upct.cpcd.indieopen.utils;

import static es.upct.cpcd.indieopen.utils.StringUtils.areStringValid;
import static es.upct.cpcd.indieopen.utils.StringUtils.isStringValid;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectUtils {

    private ObjectUtils() {

    }

    public static <T> T requireNonNull(T obj, String paramName) {
        if (obj == null)
            throw new IllegalArgumentException(paramName + " must not be null.");
        return obj;
    }

    public static <T> void requireNonNull(T obj) {
        if (obj == null)
            throw new IllegalArgumentException("Parameter must not be null.");
    }

    public static void requireStringValid(String value) {
        if (!isStringValid(value))
            throw new IllegalArgumentException("method param must be a valid string.");

    }

    public static String requireStringValid(String value, String paramName) {
        if (!isStringValid(value))
            throw new IllegalArgumentException(paramName + " must be a valid string.");

        return value;
    }

    public static void requireTrue(boolean condition, String message) {
        if (!condition)
            throw new IllegalArgumentException(message);

    }

    public static void requireStringsValid(String... values) {
        if (!areStringValid(values))
            throw new IllegalArgumentException("Values must be valid strings");

    }

    public static <T> void requireNonEmpty(Collection<T> collectionOfItem, String paramName) {
        if (collectionOfItem == null || (collectionOfItem.isEmpty()))
            throw new IllegalArgumentException(paramName + " must not be empty.");

    }

    public static <T> T[] requireArrayNonEmpty(T[] array, String paramName) {
        if (array == null || (array.length == 0))
            throw new IllegalArgumentException(paramName + " must not be empty.");

        return array;
    }

    public static void requireRegexMatch(String value, String paramName, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);

        if (!matcher.matches())
            throw new IllegalArgumentException(paramName + " must match the regex: " + regex);

    }

    public static void paramGreaterThan(int param, int value) {
        if (param <= value)
            throw new IllegalArgumentException(param + " must be greater than " + value);

    }

}

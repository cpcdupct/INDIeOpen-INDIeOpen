package es.upct.cpcd.indieopen.utils;

import java.text.MessageFormat;

public class SpecificationUtils {

    private SpecificationUtils() {

    }

    public static String contains(String expression) {
        return MessageFormat.format("%{0}%", expression.toLowerCase());
    }
}
package es.upct.cpcd.indieopen.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Languages available in INDIeOpen
 */
public enum Language {
    ENGLISH("en"), FRENCH("fr"), SPANISH("es"), GREEK("el"), LITHUANIAN("lt");

    private static final Map<String, Language> ENUM_MAP;

    private final String value;

    Language(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Language get(String name) {
        return ENUM_MAP.get(name);
    }

    public static Language getDefault() {
        return ENGLISH;
    }

    static {
        Map<String, Language> map = new HashMap<>();

        for (Language instance : Language.values()) {
            map.put(instance.getValue(), instance);
        }

        ENUM_MAP = Collections.unmodifiableMap(map);
    }


}

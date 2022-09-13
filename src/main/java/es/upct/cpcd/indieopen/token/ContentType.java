package es.upct.cpcd.indieopen.token;

import java.util.HashMap;
import java.util.Map;

public enum ContentType {
    CONTENT("CONTENT"), EVALUATION("EVALUATION"), VIDEO("VIDEO"), COURSE("COURSE");

    // Lookup table
    private static final Map<String, ContentType> TYPES = new HashMap<>();

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    static {
        for (ContentType contentType : ContentType.values()) {
            TYPES.put(contentType.getValue(), contentType);
        }
    }

    public static ContentType get(String value) {
        return TYPES.get(value);
    }
}
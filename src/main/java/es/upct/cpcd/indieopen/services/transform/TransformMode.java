package es.upct.cpcd.indieopen.services.transform;

import java.util.HashMap;
import java.util.Map;

public enum TransformMode {
    PREVIEW("Preview"), OPEN("Open"), INTEROPERABILITY("Interoperability");

    // Lookup table
    private static final Map<String, TransformMode> TYPES = new HashMap<>();

    private final String value;

    TransformMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    static {
        for (TransformMode mode : TransformMode.values()) {
            TYPES.put(mode.getValue(), mode);
        }
    }

    public static TransformMode get(String value) {
        return TYPES.get(value);
    }

}

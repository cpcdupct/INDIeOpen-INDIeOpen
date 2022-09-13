package es.upct.cpcd.indieopen.questions.beans;

import java.util.HashMap;
import java.util.Map;

public enum QuestionType {
    SINGLE("SingleAnswer"), MULTIPLE("MultipleAnswer"), TRUE_FALSE("TrueFalse");

    private static final Map<String, QuestionType> TYPES = new HashMap<>();

    private final String value;

    QuestionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    static {
        for (QuestionType type : QuestionType.values()) {
            TYPES.put(type.getValue(), type);
        }
    }

    public static QuestionType get(String value) {
        return TYPES.get(value);
    }

    public static boolean isType(String type) {
        return TYPES.containsKey(type);
    }

    public static String getDiscriminator(String type) {
        QuestionType questionType = get(type);
        switch (questionType) {
            default:
            case MULTIPLE:
                return "MA";
            case SINGLE:
                return "SA";
            case TRUE_FALSE:
                return "TF";
        }
    }

}
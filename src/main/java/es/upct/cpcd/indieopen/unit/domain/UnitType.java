package es.upct.cpcd.indieopen.unit.domain;

import java.util.HashMap;
import java.util.Map;

import es.upct.cpcd.indieopen.services.document.DocumentDBCollection;

public enum UnitType {
	CONTENT("CONTENT"), EVALUATION("EVALUATION");

	// Lookup table
	private static final Map<String, UnitType> TYPES = new HashMap<>();

	private final String value;

	UnitType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	static {
		for (UnitType contentType : UnitType.values()) {
			TYPES.put(contentType.getValue(), contentType);
		}
	}

	public static UnitType get(String value) {
		return TYPES.get(value);
	}

	public static DocumentDBCollection collectionOf(UnitType type) {
		if (type == CONTENT)
			return DocumentDBCollection.CONTENT_UNITS;
		else
			return DocumentDBCollection.EVALUATION_UNITS;

	}
}
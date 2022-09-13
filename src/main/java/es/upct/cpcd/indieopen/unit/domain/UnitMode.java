package es.upct.cpcd.indieopen.unit.domain;

import java.util.HashMap;
import java.util.Map;

public enum UnitMode {

	ORIGINAL("ORIGINAL"), COPIED("COPIED"), REUSED("REUSED");

	private static final Map<String, UnitMode> TYPES = new HashMap<>();

	private final String value;

	UnitMode(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	static {
		for (UnitMode UnitMode : UnitMode.values()) {
			TYPES.put(UnitMode.getValue(), UnitMode);
		}
	}

	public static UnitMode get(String value) {
		return TYPES.get(value);
	}


}

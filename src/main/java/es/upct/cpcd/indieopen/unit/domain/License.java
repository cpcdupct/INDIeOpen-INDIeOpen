package es.upct.cpcd.indieopen.unit.domain;

import java.util.HashMap;
import java.util.Map;

public enum License {
	PRIVATE("PRIVATE"), ALLOW_READ_ONLY("ALLOW_READ_ONLY"), ALLOW_REUSE("ALLOW_REUSE");

	// Lookup table
	private static final Map<String, License> TYPES = new HashMap<>();

	private final String value;

	License(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	static {
		for (License license : License.values()) {
			TYPES.put(license.getValue(), license);
		}
	}

	public static License get(String value) {
		return TYPES.get(value);
	}
}
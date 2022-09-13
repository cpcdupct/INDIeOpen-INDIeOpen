package es.upct.cpcd.indieopen.unit.domain;

import java.util.HashMap;
import java.util.Map;

public enum CreativeCommons {

	PRIVATE("PRIVATE"), BY("BY"), BYSA("BYSA"), BYND("BYND"), BYNC("BYNC"), BYNCSA("BYNCSA"), BYNCND("BYNCND");

	private static final Map<String, CreativeCommons> TYPES = new HashMap<>();

	private final String value;

	CreativeCommons(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	static {
		for (CreativeCommons cc : CreativeCommons.values()) {
			TYPES.put(cc.getValue(), cc);
		}
	}

	public static CreativeCommons get(String value) {
		return TYPES.get(value);
	}

	public static License fromLicense(CreativeCommons ccLicense) {
		switch (ccLicense) {
		case BYND:
		case BYNCND:
			return License.ALLOW_READ_ONLY;
		case BYNCSA:
		case BYNC:
		case BYSA:
		case BY:
			return License.ALLOW_REUSE;
		case PRIVATE:
		default:
			return License.PRIVATE;
		}
	}

}

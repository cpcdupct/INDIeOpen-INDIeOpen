package es.upct.cpcd.indieopen.utils;

import java.util.UUID;

public class ModelUtils {

	private ModelUtils() {

	}

	public static String randomUUID(boolean removeDash) {
		return removeDash ? UUID.randomUUID().toString().replace("-", "") : UUID.randomUUID().toString();
	}

}

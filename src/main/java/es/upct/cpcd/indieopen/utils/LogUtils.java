package es.upct.cpcd.indieopen.utils;

import org.apache.logging.log4j.Logger;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;

public class LogUtils {

	private LogUtils() {

	}

	public static void log(Logger logInstance, INDIeException e, String message) {
		if (e.getStatus() == Status.INTERNAL_ERROR)
			logInstance.error(message, e);
		else if (e.getStatus() == Status.USER_ERROR || e.getStatus() == Status.UNAUTHORIZED)
			logInstance.info(message, e);
	}

	public static void log(Logger logInstance, INDIeException e) {
		log(logInstance, e, StringUtils.EMPTY_STRING);
	}
}

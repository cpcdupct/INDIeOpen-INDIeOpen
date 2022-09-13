package es.upct.cpcd.indieopen.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDateTime;

/**
 * DateUtils
 */
public class DateUtils {
	private DateUtils() {

	}

	public static String dateToISOString(LocalDateTime date) {
		if (date == null)
			return null;

		return OffsetDateTime.ofInstant(date.toInstant(ZoneOffset.of("+01:00")), ZoneId.of("Europe/Madrid")).toString();
	}

	public static LocalDateTime dateParseFromISOString(String dateString) {
		return OffsetDateTime.parse(dateString).toLocalDateTime();
	}

	public static LocalDateTime createDate(int day, int month, int year) {
		return LocalDateTime.of(year, month, day, 9, 0);
	}

	public static boolean sameDay(LocalDateTime date1, LocalDateTime date2) {
		ObjectUtils.requireNonNull(date1);
		ObjectUtils.requireNonNull(date2);

		return ((date1.getDayOfMonth() == date2.getDayOfMonth()) && (date1.getYear() == date2.getYear())
				&& (date1.getMonthValue() == date2.getMonthValue()));
	}

	public static boolean before(LocalDateTime date, LocalDateTime expirationDate) {
		ObjectUtils.requireNonNull(date);
		ObjectUtils.requireNonNull(expirationDate);

		return (date.isBefore(ChronoLocalDateTime.from(expirationDate)));
	}

}
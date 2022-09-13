package es.upct.cpcd.indieopen.services.publish;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.upct.cpcd.indieopen.utils.ObjectUtils;

@Service
public class TimestampGenerator {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final String SEPARATOR = "|";

	/** Secret key */
	private final String secretKey;

	public TimestampGenerator(@Value("${publish.secret}") String key) {
		this.secretKey = key;

	}

	public String generateTimestamp(LocalDateTime time, String teacherId) {
		ObjectUtils.requireNonNull(time);
		ObjectUtils.requireStringValid(teacherId);
		String dateFormatted = time.format(formatter);
		String plainContent = dateFormatted + SEPARATOR + teacherId;

		AesCipher encrypted = AesCipher.encrypt(secretKey, plainContent);

		return encrypted.getData();
	}

}

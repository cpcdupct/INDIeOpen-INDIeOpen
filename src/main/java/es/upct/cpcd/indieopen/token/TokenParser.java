package es.upct.cpcd.indieopen.token;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.upct.cpcd.indieopen.common.exceptions.ErrorCodes;
import es.upct.cpcd.indieopen.common.exceptions.ErrorField;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.services.cipher.CipherException;
import es.upct.cpcd.indieopen.services.cipher.CipherService;
import es.upct.cpcd.indieopen.utils.DateUtils;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class TokenParser {
	private static final String TOKEN_SEPARATOR = "&";
	private static final long DAYS = 3;
	private final CipherService cipherService;

	@Autowired
	public TokenParser(CipherService cipherService) {
		this.cipherService = cipherService;
	}

	public String generateToken(int entityId, String userId, ContentType type) throws INDIeException {
		String plainToken = generatePlainToken(entityId, userId, type, LocalDateTime.now().plusDays(DAYS));
		try {
			return cipherService.encrypt(plainToken);
		} catch (CipherException e) {
			throw INDIeExceptionFactory.createInternalException(e);
		}
	}

	private String generatePlainToken(int entityId, String userId, ContentType type, LocalDateTime dateTime) {
		return separateStrings(String.valueOf(entityId), String.valueOf(userId), DateUtils.dateToISOString(dateTime),
				type.getValue(), UUID.randomUUID().toString());
	}

	private String separateStrings(String... strings) {
		StringBuilder stBuilder = new StringBuilder();
		stBuilder.append(strings[0]);

		for (int i = 1; i < strings.length; i++)
			stBuilder.append(TOKEN_SEPARATOR).append(strings[i]);

		return stBuilder.toString();
	}

	public ModelToken parseToken(String token) throws INDIeException {
		try {
			String plainToken = cipherService.decrypt(token);
			ModelToken modelToken = parsePlainToken(token, plainToken);

			if (modelToken.isExpired()) {
				throw new INDIeExceptionBuilder(token + " is not valid").code(ErrorCodes.EDITOR_TOKEN_NOT_VALID)
						.status(Status.USER_ERROR).errorFields(ErrorField.listOf("token", "Token is expired")).build();
			}

			return modelToken;
		} catch (INDIeException e) {
			throw e;
		} catch (Exception ex) {
			log.error("error in parseToken ", ex);
			throw new INDIeExceptionBuilder(token + " is not valid").code(ErrorCodes.EDITOR_TOKEN_NOT_VALID)
					.status(Status.USER_ERROR).errorFields(ErrorField.listOf("token", "Token is not valid")).build();
		}
	}

	private ModelToken parsePlainToken(String encryptedToken, String plainToken) {
		String[] values = plainToken.split(TOKEN_SEPARATOR);
		return new ModelToken(encryptedToken, Integer.parseInt(values[0]), values[1],
				DateUtils.dateParseFromISOString(values[2]), ContentType.get(values[3]));
	}
}
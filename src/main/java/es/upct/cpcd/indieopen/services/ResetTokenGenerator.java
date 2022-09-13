package es.upct.cpcd.indieopen.services;

import static es.upct.cpcd.indieopen.utils.ModelUtils.randomUUID;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.services.cipher.CipherException;
import es.upct.cpcd.indieopen.services.cipher.CipherService;
import es.upct.cpcd.indieopen.utils.DateUtils;

@Service
public class ResetTokenGenerator {
    private static final int EXPIRE_DAYS = 3;
    private static final String SEPARATOR = ";";

    private final CipherService cipherService;

    @Autowired
    public ResetTokenGenerator(CipherService cipherService) {
        this.cipherService = cipherService;
    }

    public String generateToken(String user) throws INDIeException {
        String plainString = getPlainString(user, LocalDateTime.now().plusDays(EXPIRE_DAYS));
        try {
            return cipherService.encrypt(plainString);
        } catch (CipherException e) {
            throw INDIeExceptionFactory.createInternalException(e);
        }
    }

    public boolean isTokenValid(String user, String token) throws INDIeException {
        try {
            String decrypted = cipherService.decrypt(token);
            TokenData tokenData = new TokenData(decrypted);
            return tokenData.isValid(user);
        } catch (CipherException e) {
            throw INDIeExceptionFactory.createInternalException(e);
        }

    }

    public String getPlainString(String user, LocalDateTime dateTime) {
        String randomToken = randomUUID(true);
        return randomToken + SEPARATOR + user + SEPARATOR + DateUtils.dateToISOString(dateTime);
    }

    static class TokenData {
        private final String user;
        private final LocalDateTime expireAt;

        TokenData(String plainString) {
            String[] values = plainString.split(SEPARATOR);

            String userValue = values[1];
            LocalDateTime expire = DateUtils.dateParseFromISOString(values[2]);

            this.user = userValue;
            this.expireAt = expire;
        }

        public String getUser() {
            return user;
        }

        public boolean isValid(String user) {
            return (LocalDateTime.now().isBefore(expireAt) && this.user.equals(user));
        }
    }
}
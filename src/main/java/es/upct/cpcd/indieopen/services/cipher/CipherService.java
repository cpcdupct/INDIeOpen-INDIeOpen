package es.upct.cpcd.indieopen.services.cipher;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CipherService {

    private final String key;
    private final String initVector;

    public CipherService(@Value("${cipher.key}") String key, @Value("${cipher.initVector}") String initVector) {
        this.key = key;
        this.initVector = initVector;
    }

    public String encrypt(String value) throws CipherException {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "SPEC");
            Cipher cipher = Cipher.getInstance("CONFIGURATION");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new CipherException(ex);
        }
    }

    public String decrypt(String encrypted) throws CipherException {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "SPEC");
            Cipher cipher = Cipher.getInstance("CONFIGURATION");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.getUrlDecoder().decode(encrypted));
            return new String(original);
        } catch (Exception ex) {
            throw new CipherException(ex);
        }
    }
}

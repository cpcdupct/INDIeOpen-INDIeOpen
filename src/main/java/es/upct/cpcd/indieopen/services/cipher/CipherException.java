package es.upct.cpcd.indieopen.services.cipher;

public class CipherException extends Exception {
    private static final long serialVersionUID = 1L;

    public CipherException(Exception ex) {
        super(ex);
    }

}

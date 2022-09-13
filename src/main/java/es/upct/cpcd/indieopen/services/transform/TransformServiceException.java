package es.upct.cpcd.indieopen.services.transform;

public class TransformServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    public TransformServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TransformServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformServiceException(String message) {
        super(message);
    }

    public TransformServiceException(Throwable cause) {
        super(cause);
    }

}

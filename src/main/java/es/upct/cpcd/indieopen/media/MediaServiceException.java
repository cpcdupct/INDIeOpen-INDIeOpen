package es.upct.cpcd.indieopen.media;

public class MediaServiceException extends Exception {
	private static final long serialVersionUID = 1L;

	public MediaServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public MediaServiceException(Throwable cause) {
		super(cause);
	}

}

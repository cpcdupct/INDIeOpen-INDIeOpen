package es.upct.cpcd.indieopen.services.mail;

public class MailServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public MailServiceException(String message) {
		super(message);
	}

	public MailServiceException(Throwable cause) {
		super(cause);
	}

	public MailServiceException(String message, Throwable cause) {
		super(message, cause);
	}

}

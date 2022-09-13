package es.upct.cpcd.indieopen.services.publish;

public class PublishServiceException extends Exception {
	private static final long serialVersionUID = 1L;

	PublishServiceException() {
		super();
	}

	PublishServiceException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	PublishServiceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	PublishServiceException(String arg0) {
		super(arg0);
	}

	PublishServiceException(Throwable arg0) {
		super(arg0);
	}

}

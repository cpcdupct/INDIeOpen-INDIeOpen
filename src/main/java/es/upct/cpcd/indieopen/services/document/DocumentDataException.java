package es.upct.cpcd.indieopen.services.document;

/**
 * Exception produced in {@link DocumentDBManager} methods. It represents an
 * internal error accessing the Document-based database.
 * 
 * @author mario
 *
 */
public class DocumentDataException extends Exception {

	private static final long serialVersionUID = 1L;

	public DocumentDataException() {
		super();
	}

	public DocumentDataException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public DocumentDataException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DocumentDataException(String arg0) {
		super(arg0);
	}

	public DocumentDataException(Throwable arg0) {
		super(arg0);
	}

}

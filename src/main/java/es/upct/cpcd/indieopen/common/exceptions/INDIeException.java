package es.upct.cpcd.indieopen.common.exceptions;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * Root exception int the whole INDIe service
 *
 * @author CPCD
 *
 */
@Setter
@Getter
public class INDIeException extends Exception {
	private static final long serialVersionUID = 1L;
	/** Application error code */
	private String code = ErrorCodes.INTERNAL_ERROR;

	/** Error status */
	private Status status = Status.INTERNAL_ERROR;

	/** List of error fields of a form */
	private List<ErrorField> errorFields;

	/**
	 * Basic constructor
	 */
	public INDIeException() {
		super();
	}

	/**
	 * Create an INDIeException with a message
	 * 
	 * @param msg Message
	 */
	public INDIeException(String msg) {
		super(msg);
	}

	/**
	 * Create an INDIeException with a reason
	 * 
	 * @param reason reason
	 */
	public INDIeException(Throwable reason) {
		super(reason);
	}

	/**
	 * Create an INDIeException with a message and a reason
	 * 
	 * @param msg
	 * @param reason
	 */
	public INDIeException(String msg, Throwable reason) {
		super(msg, reason);
	}

	/**
	 * Retunrs the error code String associated with the response that the service
	 * must return when this exception is produced. See ErrorCodes class.
	 *
	 * @return HTTP code response
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * Returns the HTTP status code .
	 *
	 * @return HTTP status code
	 */
	public int getStatusNumber() {
		return status.getValue();
	}

	/**
	 * Returns a list of ErrorField with the parameters that are wrong in the
	 * request. If not implemented by subclasses, it will return an Empty List
	 *
	 * @return List of ErrorField
	 */
	public List<ErrorField> getErrorFields() {
		if (this.errorFields == null)
			return Collections.emptyList();

		return this.errorFields;
	}

	/**
	 * Status wrapper for HTTP status codes
	 * 
	 * @author CPCD
	 *
	 */
	public enum Status {
		USER_ERROR(400), INTERNAL_ERROR(500), UNAUTHORIZED(403), NOT_FOUND(404);

		/** Status value */
		private final int value;

		/** Create a status from a value */
		Status(int value) {
			this.value = value;
		}

		/** Get status value */
		public int getValue() {
			return value;
		}

	}

	@Override
	public String toString() {
		return "INDIeException [message=" + this.getMessage() + ", code=" + code + ", status=" + status
				+ ", errorFields=" + errorFields + "]";
	}

}
package es.upct.cpcd.indieopen.common.exceptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.validation.FieldError;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a field provided by the consumer that has an error
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorField implements Serializable {
	private static final long serialVersionUID = "MY_SERIAL_VERSION";

	/**
	 * Field name
	 */
	private String field;
	/**
	 * Error message
	 */
	private String message;

	/**
	 * Create a List of ErrorField containing only one ErrorField with the specified
	 * parameters.
	 *
	 * @param field   Field name
	 * @param message Error message
	 * @return List of ErrorField
	 */
	public static List<ErrorField> listOf(String field, String message) {
		ErrorField err = new ErrorField();
		err.field = field;
		err.message = message;
		ArrayList<ErrorField> list = new ArrayList<>();
		list.add(err);

		return list;
	}

	/**
	 * Create a List of ErrorField containing the ErrorField instances specified in
	 * the parameters.
	 *
	 * @param err Array of ErrorField instances
	 * @return List of ErrorField
	 */
	public static List<ErrorField> listOf(ErrorField... err) {
		return new ArrayList<>(Arrays.asList(err));
	}

	/**
	 * Creates an ErrorField from a FieldError instance of the spring framework
	 * validation package
	 *
	 * @param fieldError FieldError instance
	 * @return ErrorField instance
	 */
	public static ErrorField errorFromField(FieldError fieldError) {
		ErrorField errorField = new ErrorField();

		errorField.setField(fieldError.getField());
		errorField.setMessage(fieldError.getDefaultMessage());

		return errorField;
	}

	/**
	 * Creates an inmutable empty list of error fields
	 * 
	 * @return Inmutable empty list of Error Fields
	 */
	public static List<ErrorField> emptyList() {
		return Collections.emptyList();
	}

}
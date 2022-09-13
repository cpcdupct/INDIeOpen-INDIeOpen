package es.upct.cpcd.indieopen.infraestructure;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import es.upct.cpcd.indieopen.common.exceptions.ErrorCodes;
import es.upct.cpcd.indieopen.common.exceptions.ErrorField;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.response.ErrorResponse;
import lombok.extern.log4j.Log4j2;

/**
 * Class to override the global exception handler
 */
@ControllerAdvice
@Log4j2
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
		// Create the response
		ErrorResponse response = new ErrorResponse("Request is not valid", ErrorCodes.WRONG_PARAMS,
				HttpStatus.BAD_REQUEST.value());

		List<ErrorField> errors = e.getConstraintViolations().stream()
				.map(c -> new ErrorField(c.getPropertyPath().toString(), c.getMessage())).collect(Collectors.toList());

		log.error("Constraint violation", e);

		response.setErrors(errors);
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler({ INDIeException.class })
	public ResponseEntity<Object> handleFacadeException(final INDIeException ex, final WebRequest request) {
		ErrorResponse response = new ErrorResponse(ex.getMessage(), ex.getCode(), ex.getStatusNumber());
		response.setErrors(ex.getErrorFields());
		log.error("INDIe Exception", ex);
		return ResponseEntity.status(ex.getStatusNumber()).body(response);
	}

	// Other exception handler
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
		ErrorResponse response = new ErrorResponse("Internal error", ErrorCodes.INTERNAL_ERROR,
				HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setErrors(Collections.emptyList());

		logger.error("Error captured in global handler", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
	}
}

package es.upct.cpcd.indieopen.common.exceptions;

import java.util.List;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;

public class INDIeExceptionFactory {

	private INDIeExceptionFactory() {

	}

	public static INDIeException createUnsafePasswordException() {
		return new INDIeExceptionBuilder("The password provided is not safe enough").code(ErrorCodes.UNSAFE_PASSWORD)
				.status(Status.USER_ERROR).errorFields(ErrorField.listOf("password", "Password is not safe")).build();
	}

	public static INDIeException createUserNotFoundException(String user) {
		return new INDIeExceptionBuilder("The user" + user + " not found").code(ErrorCodes.ENTITY_NOT_ACCESSIBLE)
				.status(Status.USER_ERROR).errorFields(ErrorField.listOf("username", "User not found")).build();
	}

	public static INDIeException createDuplicatedUserException(String email) {
		return new INDIeExceptionBuilder("The user with email:" + email + " already exists")
				.code(ErrorCodes.EXISTING_USER).status(Status.USER_ERROR)
				.errorFields(ErrorField.listOf("email", "User already exists")).build();
	}

	public static INDIeException createUnitNotFound(int unitId) {
		return new INDIeExceptionBuilder("Unit with id:" + unitId + " not found").code(ErrorCodes.ENTITY_NOT_ACCESSIBLE)
				.status(Status.USER_ERROR).errorFields(ErrorField.listOf("unit", "Unit not found")).build();
	}
	
	public static INDIeException createUnitResourceNotFound(String unitResource) {
		return new INDIeExceptionBuilder("Unit with resource:" + unitResource + " not found").code(ErrorCodes.ENTITY_NOT_ACCESSIBLE)
				.status(Status.USER_ERROR).errorFields(ErrorField.listOf("unit", "Unit not found")).build();
	}


	public static INDIeException createUnitNotAccessible(int unitId) {
		return new INDIeExceptionBuilder("Unit with id:" + unitId + " not accessible")
				.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).status(Status.USER_ERROR)
				.errorFields(ErrorField.listOf("unit", "Unit not accesible for the user")).build();
	}

	public static INDIeException createEntityNotFound(String entity) {
		return new INDIeExceptionBuilder("Entity not found: " + entity).code(ErrorCodes.ENTITY_NOT_ACCESSIBLE)
				.status(Status.NOT_FOUND).build();
	}

	public static INDIeException createInternalException(String string) {
		return new INDIeExceptionBuilder(string).build();
	}

	public static INDIeException createInternalException(Exception exception) {
		return new INDIeExceptionBuilder(exception).build();
	}

	public static INDIeException createCategoryNotFoundException(int category) {
		return new INDIeExceptionBuilder("Category not found:" + category).code(ErrorCodes.ENTITY_NOT_ACCESSIBLE)
				.status(Status.USER_ERROR).errorFields(ErrorField.listOf("category", "Category not found")).build();
	}

	public static INDIeException createQuestionNotFoundException(String questionId) {
		return new INDIeExceptionBuilder(questionId + " question not found").code(ErrorCodes.ENTITY_NOT_ACCESSIBLE)
				.status(Status.USER_ERROR).errorFields(ErrorField.listOf("question", "question is not valid")).build();
	}

	public static INDIeException createUnauthorizeRequest() {
		return new INDIeExceptionBuilder("Unauthorized request").code(ErrorCodes.UNAUTHORIZED_REQUEST)
				.status(Status.UNAUTHORIZED).build();
	}

	public static INDIeException createUserRequestError(IllegalArgumentException e) {
		return new INDIeExceptionBuilder("Request params are invalid", e).code(ErrorCodes.WRONG_PARAMS)
				.status(Status.USER_ERROR).build();
	}

	public static INDIeException createUserRequestError(Object parameter) {
		return new INDIeExceptionBuilder("Request params are invalid " + parameter).code(ErrorCodes.WRONG_PARAMS)
				.status(Status.USER_ERROR).build();
	}

	public static INDIeException createDuplicatedGroupException(String groupName) {
		return new INDIeExceptionBuilder("Duplicated group for: " + groupName).code(ErrorCodes.WRONG_PARAMS)
				.status(Status.USER_ERROR).build();
	}

	public static INDIeException createInvalidToken(String token) {
		return new INDIeExceptionBuilder("Token is invalid " + token).code(ErrorCodes.WRONG_PARAMS)
				.status(Status.USER_ERROR).build();
	}

	public static INDIeException createWrongParamsException(List<ErrorField> errors) {
		return new INDIeExceptionBuilder("Request params are invalid").code(ErrorCodes.WRONG_PARAMS).errorFields(errors)
				.status(Status.USER_ERROR).build();
	}
}

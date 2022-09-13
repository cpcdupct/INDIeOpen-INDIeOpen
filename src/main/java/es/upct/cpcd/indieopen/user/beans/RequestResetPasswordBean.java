package es.upct.cpcd.indieopen.user.beans;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RequestResetPasswordBean {

	@Email
	private String email;

	@NotNull
	private String language;
}

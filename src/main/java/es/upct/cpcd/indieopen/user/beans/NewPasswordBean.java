package es.upct.cpcd.indieopen.user.beans;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class NewPasswordBean {

	@NotBlank
	private String password;

}

package es.upct.cpcd.indieopen.user.beans;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class CreateUserBean {

	@NotNull
	@Email
	private String email;

	@NotNull
	private String password;

	@NotBlank
	@Size(min = 1, max = 80, message = "Size (0,80)")
	private String name;

	@NotBlank
	@Size(min = 1, max = 80, message = "Size (0,80)")
	private String lastName;

	@NotBlank
	@Size(min = 0, max = 90, message = "Size (0,90)")
	private String country;

	@NotBlank
	@Size(min = 0, max = 150, message = "Size (0,150)")
	private String institution;

	@NotNull
	private String language;
}

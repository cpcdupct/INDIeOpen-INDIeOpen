package es.upct.cpcd.indieopen.user.beans;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserInfoBean {

	@NotBlank
	@Size(min = 1, max = 80, message = "Size (1,120)")
	private String name;

	@NotBlank
	@Size(min = 1, message = "Size (1,120)")
	private String surname;

	@Size(max = 90, message = "Size (1,120)")
	private String country;

	@Size(max = 150, message = "Size (1,120)")
	private String institution;

	@NotBlank
	@URL
	private String avatar;

	@Size(max = 300, message = "Size (1,120)")
	private String biography;

}

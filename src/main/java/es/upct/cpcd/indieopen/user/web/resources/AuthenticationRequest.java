package es.upct.cpcd.indieopen.user.web.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = "MY_SERIAL_VERSION";
	private String username;
    private String password;
}
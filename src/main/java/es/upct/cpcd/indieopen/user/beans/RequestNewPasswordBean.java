package es.upct.cpcd.indieopen.user.beans;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import es.upct.cpcd.indieopen.common.Language;
import lombok.Data;

@Data
public class RequestNewPasswordBean {

    @Email(message = "email must be valid")
    private String email;

    @NotNull
    private Language language;

}
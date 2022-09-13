package es.upct.cpcd.indieopen.services;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServiceException extends INDIeException {
    private static final long serialVersionUID = 1L;

    ServiceException(String arg0) {
        super(arg0);
    }

    ServiceException(Throwable arg0) {
        super(arg0);
    }

    ServiceException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}

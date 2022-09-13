package es.upct.cpcd.indieopen.common.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import es.upct.cpcd.indieopen.common.exceptions.ErrorField;
import es.upct.cpcd.indieopen.utils.DateUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Error Wrapper class. Encapsulates the produced error and tell the consumer of
 * the services what has gone wrong and if it can do something about.
 */
@Getter
@Setter
public class ErrorResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Short message about the error
     */
    private String msg;
    /**
     * Error key
     */
    private String errorCode;
    /**
     * Request status
     */
    private int status;
    /**
     * Error timestamp
     */
    private String timestamp;
    /**
     * List of Error Fields that occured in the request, if present.
     */
    @Getter(value = AccessLevel.NONE)
    private List<ErrorField> errors;

    /**
     * Constructor of Error Response
     *
     * @param msg       Short message of the error
     * @param errorCode Error code
     * @param status    Status code
     */
    public ErrorResponse(String msg, String errorCode, int status) {
        this.msg = msg;
        this.errorCode = errorCode;
        this.status = status;
        this.timestamp = DateUtils.dateToISOString(LocalDateTime.now());
    }

    /**
     * Get the list of errors. List can be empty
     *
     * @return List of ErrorField instances
     */
    public List<ErrorField> getErrors() {
        if (errors == null)
            return Collections.emptyList();

        return errors;
    }

}
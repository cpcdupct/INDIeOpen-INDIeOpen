package es.upct.cpcd.indieopen.common.exceptions;

import java.util.List;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;

public class INDIeExceptionBuilder {

    private final INDIeException exception;

    public INDIeExceptionBuilder(String msg) {
        this.exception = new INDIeException(msg);
    }

    public INDIeExceptionBuilder(Throwable thr) {
        this.exception = new INDIeException(thr);
    }

    public INDIeExceptionBuilder(String msg, Throwable thr) {
        this.exception = new INDIeException(msg, thr);
    }

    public INDIeExceptionBuilder code(String code) {
        this.exception.setCode(code);

        return this;
    }

    public INDIeExceptionBuilder status(Status status) {
        this.exception.setStatus(status);

        return this;
    }

    public INDIeExceptionBuilder errorFields(List<ErrorField> errorFields) {
        this.exception.setErrorFields(errorFields);

        return this;
    }

    public INDIeException build() {
        return this.exception;
    }

}
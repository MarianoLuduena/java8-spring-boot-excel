package ar.com.itau.seed.config.exception;

import ar.com.itau.seed.config.ErrorCode;

public class UnknownValueException extends GenericException {

    public UnknownValueException(ErrorCode errorCode) {
        super(errorCode);
    }

}

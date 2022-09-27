package ar.com.itau.seed.application.service;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class ValidationService {

    private final Validator validator;

    public ValidationService(final Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T input) {
        final Set<ConstraintViolation<T>> violations = doFindViolations(input);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public <T> Set<ConstraintViolation<T>> findViolations(T input) {
        return doFindViolations(input);
    }

    private <T> Set<ConstraintViolation<T>> doFindViolations(T input) {
        return validator.validate(input);
    }

}

package ar.com.itau.seed.domain.error;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Value
public class EntityError {

    String id;
    List<Error> errors;

    @Getter
    @ToString
    @EqualsAndHashCode
    public static class Error {
        public static Error parsing(final String fieldId, final String description) {
            return new Error(ErrorType.PARSING, fieldId, description);
        }

        public static <T> List<Error> validation(final Set<ConstraintViolation<T>> violations) {
            return violations.stream().map(violation -> {
                final String fieldId = violation.getPropertyPath().toString();
                final String description = violation.getMessage();
                return new Error(ErrorType.VALIDATION, fieldId, description);
            }).collect(Collectors.toList());
        }

        private final ErrorType type;
        private final String fieldId;
        private final String description;

        private Error(
                final ErrorType type,
                final String fieldId,
                final String description
        ) {
            this.type = type;
            this.fieldId = fieldId;
            this.description = description;
        }
    }

}

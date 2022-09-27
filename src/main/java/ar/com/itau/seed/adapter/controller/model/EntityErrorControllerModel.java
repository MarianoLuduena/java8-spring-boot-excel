package ar.com.itau.seed.adapter.controller.model;

import ar.com.itau.seed.adapter.controller.mapper.ControllerMapper;
import ar.com.itau.seed.domain.error.EntityError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.util.Set;
import java.util.stream.Collectors;

@Value
@Builder
public class EntityErrorControllerModel {

    private static final ControllerMapper MAPPER = new ControllerMapper();

    public static <T> EntityErrorControllerModel from(final EntityError domain) {
        return EntityErrorControllerModel.builder()
                .source(domain.getId())
                .errors(domain.getErrors().stream().map(ErrorControllerModel::from).collect(Collectors.toSet()))
                .build();
    }

    String source;
    Set<ErrorControllerModel> errors;

    @AllArgsConstructor
    @Builder
    @Getter
    @EqualsAndHashCode
    public static class ErrorControllerModel {
        public static ErrorControllerModel from(final EntityError.Error domain) {
            return ErrorControllerModel.builder()
                    .type(MAPPER.fromErrorType(domain.getType()))
                    .fieldId(domain.getFieldId())
                    .description(domain.getDescription())
                    .build();
        }

        String type;
        String fieldId;
        String description;
    }

}

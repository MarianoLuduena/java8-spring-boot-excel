package ar.com.itau.seed.adapter.controller.model;

import ar.com.itau.seed.domain.AccountsBatch;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
public class AccountsFileUploadControllerModel {

    public static AccountsFileUploadControllerModel from(final AccountsBatch domain) {
        return AccountsFileUploadControllerModel.builder()
                .id(domain.getId())
                .sourceName(domain.getSourceName())
                .errors(domain.getErrors().stream().map(EntityErrorControllerModel::from).collect(Collectors.toList()))
                .processed(domain.errorCount() + domain.employeeCount())
                .build();
    }

    Long id;
    String sourceName;
    List<EntityErrorControllerModel> errors;
    Integer processed;

}

package ar.com.itau.seed.adapter.rest.model;

import ar.com.itau.seed.adapter.rest.mapper.FileMapper;
import ar.com.itau.seed.domain.AccountsBatch;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccountsBatchRestModel {

    private static final FileMapper FILE_MAPPER = new FileMapper();

    public static AccountsBatchRestModel from(final AccountsBatch domain) {
        return AccountsBatchRestModel.builder()
                .id(domain.getId())
                .content(FILE_MAPPER.toBase64(domain))
                .build();
    }

    Long id;
    String content;

}

package ar.com.itau.seed.application.port.out;

import ar.com.itau.seed.domain.AccountsBatch;

public interface SaveAccountsBatch {

    long save(final AccountsBatch accountsBatch);

}

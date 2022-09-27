package ar.com.itau.seed.adapter.db;

import ar.com.itau.seed.adapter.db.mapper.DbMapper;
import ar.com.itau.seed.adapter.db.model.AccountsBatchDbModel;
import ar.com.itau.seed.application.port.out.SaveAccountsBatch;
import ar.com.itau.seed.domain.AccountsBatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class AccountsBatchDbAdapter implements SaveAccountsBatch {

    private final DbMapper dbMapper;
    private final AccountsBatchRepository accountsBatchRepository;

    public AccountsBatchDbAdapter(
            final DbMapper dbMapper,
            final AccountsBatchRepository accountsBatchRepository
    ) {
        this.dbMapper = dbMapper;
        this.accountsBatchRepository = accountsBatchRepository;
    }

    @Override
    public long save(final AccountsBatch accountsBatch) {
        log.info("Saving batch {}", accountsBatch);
        final long startTime = System.currentTimeMillis();
        final AccountsBatchDbModel model =
                accountsBatchRepository.save(dbMapper.fromAccountsCreationBatch(accountsBatch));
        log.info("Saved batch in {} ms", System.currentTimeMillis() - startTime);
        return model.getId();
    }

}

package ar.com.itau.seed.adapter.db;

import ar.com.itau.seed.adapter.db.model.AccountsBatchDbModel;
import org.springframework.data.repository.CrudRepository;

public interface AccountsBatchRepository extends CrudRepository<AccountsBatchDbModel, Long> {
}

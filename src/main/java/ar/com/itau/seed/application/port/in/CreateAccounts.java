package ar.com.itau.seed.application.port.in;

import ar.com.itau.seed.config.util.either.Either;
import ar.com.itau.seed.domain.AccountsBatch;
import ar.com.itau.seed.domain.Employee;
import ar.com.itau.seed.domain.error.EntityError;
import lombok.NonNull;
import lombok.Value;

import java.util.stream.Stream;

public interface CreateAccounts {

    AccountsBatch execute(Cmd cmd);

    @Value
    class Cmd {
        @NonNull Stream<Either<EntityError, Employee>> maybeEmployees;
        @NonNull String sourceName;
    }

}

package ar.com.itau.seed.config.util.either;

import java.util.Optional;

public interface Either<L, R> {

    boolean isLeft();

    L left();

    boolean isRight();

    R right();

    Optional<R> toOptional();

}

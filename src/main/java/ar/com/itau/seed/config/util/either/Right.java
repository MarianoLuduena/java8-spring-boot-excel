package ar.com.itau.seed.config.util.either;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;

@ToString
@EqualsAndHashCode
public class Right<L, R> implements Either<L, R> {

    public static <L, R> Right<L, R> of(final R value) {
        return new Right<>(value);
    }

    private final R value;

    private Right(final R value) {
        this.value = value;
    }

    @Override
    public boolean isLeft() {
        return false;
    }

    @Override
    public L left() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public boolean isRight() {
        return true;
    }

    @Override
    public R right() {
        return value;
    }

    @Override
    public Optional<R> toOptional() {
        return Optional.of(value);
    }

}

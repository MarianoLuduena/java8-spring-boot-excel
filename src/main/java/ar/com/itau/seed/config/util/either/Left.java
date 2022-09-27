package ar.com.itau.seed.config.util.either;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;

@ToString
@EqualsAndHashCode
public class Left<L, R> implements Either<L, R> {

    public static <L, R> Left<L, R> of(L value) {
        return new Left<>(value);
    }

    private final L value;

    private Left(final L value) {
        this.value = value;
    }

    @Override
    public boolean isLeft() {
        return true;
    }

    @Override
    public L left() {
        return value;
    }

    @Override
    public boolean isRight() {
        return false;
    }

    @Override
    public R right() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public Optional<R> toOptional() {
        return Optional.empty();
    }

}

package com.library.strategy;

import com.library.util.Validation;

import java.util.Objects;

/**
 * Abstract base decorator for layering composable fine calculation rules
 * around any {@link FineCalculationStrategy}.
 */
public abstract class FineStrategyDecorator implements FineCalculationStrategy {

    protected final FineCalculationStrategy wrapped;

    protected FineStrategyDecorator(FineCalculationStrategy wrapped) {
        this.wrapped = Validation.requireNonNull(wrapped, "wrapped");
    }

    public FineCalculationStrategy getWrapped() {
        return wrapped;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FineStrategyDecorator that = (FineStrategyDecorator) o;
        return Objects.equals(wrapped, that.wrapped);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), wrapped);
    }
}

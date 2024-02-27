package ru.hse.server.utils;

public abstract class ValidationStep<T> {

    ValidationStep<T> nextStep;

    public ValidationStep<T> addStep(ValidationStep<T> step) {
        if (nextStep == null) {
            nextStep = step;
            return this;
        }

        nextStep.addStep(step);
        return this;
    }

    public abstract ValidationResult validate(T validatedValue);

    protected ValidationResult checkNext(T validatedValue) {
        if (nextStep == null) {
            return ValidationResult.valid();
        }

        return nextStep.validate(validatedValue);
    }
}

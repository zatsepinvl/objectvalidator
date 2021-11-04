package org.objectvalidator.samples.car;

import java.lang.Override;
import javax.validation.ConstraintViolationException;

public class CarValidatorImpl implements CarValidator {
    @Override
    public void validate(Car product) {
        if (product.getId() == null) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotNull.message}", null);
        }

        if (product.getId().isEmpty()) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotEmpty.message}", null);
        }

        if (product.getName() == null) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotNull.message}", null);
        }

        if (product.getName().isEmpty()) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotEmpty.message}", null);
        }

        if (product.getElements() == null) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotNull.message}", null);
        }

        if (product.getElements().isEmpty()) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotEmpty.message}", null);
        }
    }
}

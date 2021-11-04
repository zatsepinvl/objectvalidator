package org.objectvalidator.samples.car;

import java.lang.Override;
import javax.validation.ConstraintViolationException;

public class CarValidatorImpl implements CarValidator {
    @Override
    public void validate(Car product) {
        if (product.getId() == null) {
            throw new ConstraintViolationException("id must not be null", null);
        }

        if (product.getId().isEmpty()) {
            throw new ConstraintViolationException("id must not be empty", null);
        }

        if (product.getName() == null) {
            throw new ConstraintViolationException("name must not be null", null);
        }

        if (product.getName().isEmpty()) {
            throw new ConstraintViolationException("name must not be empty", null);
        }

        if (product.getElements() == null) {
            throw new ConstraintViolationException("elements must not be null", null);
        }

        if (product.getElements().isEmpty()) {
            throw new ConstraintViolationException("elements must not be empty", null);
        }
    }
}

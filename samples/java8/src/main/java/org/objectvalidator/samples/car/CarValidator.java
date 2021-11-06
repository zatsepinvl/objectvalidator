package org.objectvalidator.samples.car;

import org.objectvalidator.Validator;

@Validator
public interface CarValidator {

    void validate(Car product);
}
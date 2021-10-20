package org.objectvalidator.samples;

import org.objectvalidator.Validator;

@Validator
public interface ProductValidator {

    void validate(Product product);
}
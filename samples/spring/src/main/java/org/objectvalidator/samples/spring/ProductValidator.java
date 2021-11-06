package org.objectvalidator.samples.spring;

import org.objectvalidator.Validator;

import static org.objectvalidator.ComponentModel.SPRING;

@Validator(componentModel = SPRING)
public interface ProductValidator {

    void validate(Product product);
}
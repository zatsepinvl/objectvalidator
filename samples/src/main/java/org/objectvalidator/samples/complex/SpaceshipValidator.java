package org.objectvalidator.samples.complex;

import org.objectvalidator.Validator;

@Validator
public interface SpaceshipValidator {

    void validate(Spaceship spaceship);
}
package org.objectvalidator.samples.spaceship;

import org.objectvalidator.Validator;

@Validator
public interface SpaceshipValidator {

    void validate(Spaceship spaceship);
}
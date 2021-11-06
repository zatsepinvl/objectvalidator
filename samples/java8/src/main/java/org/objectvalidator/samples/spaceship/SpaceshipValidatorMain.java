package org.objectvalidator.samples.spaceship;


import javax.validation.ConstraintViolationException;

public class SpaceshipValidatorMain {

    public static void main(String[] args) {
        Spaceship spaceship = new Spaceship();
        SpaceshipValidator validator = new SpaceshipValidatorImpl();

        try {
            validator.validate(spaceship);
        } catch (ConstraintViolationException ex) {
            System.out.println("Exception message: " + ex.getMessage());
        }
    }
}
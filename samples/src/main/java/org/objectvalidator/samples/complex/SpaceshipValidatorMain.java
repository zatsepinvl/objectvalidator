package org.objectvalidator.samples.complex;


public class SpaceshipValidatorMain {

    public static void main(String[] args) {
        Spaceship spaceship = new Spaceship();
        SpaceshipValidator validator = new SpaceshipValidatorImpl();
        validator.validate(spaceship);
    }
}
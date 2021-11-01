package org.objectvalidator.samples.car;



public class CarValidatorMain {

    public static void main(String[] args) {
        Car car = new Car();
        CarValidator validator = new CarValidatorImpl();
        validator.validate(car);
    }
}
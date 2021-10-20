package org.objectvalidator.samples;


public class ObjectValidatorTester {

    public static void main(String[] args) {
        Product product = new Product();
        ProductValidator validator = new ProductValidatorImpl();
        validator.validate(product);
    }

}
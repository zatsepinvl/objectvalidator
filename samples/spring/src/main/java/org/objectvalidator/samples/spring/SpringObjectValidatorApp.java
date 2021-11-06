package org.objectvalidator.samples.spring;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.validation.ConstraintViolationException;

@SpringBootApplication
@RequiredArgsConstructor
public class SpringObjectValidatorApp implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringObjectValidatorApp.class, args);
    }

    private final ProductValidator productValidator;

    @Override
    public void run(String... args) {
        Product product = new Product();
        try {
            productValidator.validate(product);
        } catch (ConstraintViolationException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
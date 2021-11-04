# Java Object Validator

[![Java CI with Gradle](https://github.com/zatsepinvl/objectvalidator/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/zatsepinvl/objectvalidator/actions/workflows/gradle.yml)

ObjectValidator is a Java annotation processor for the generation of type-safe and performant validators for Java bean
classes.

:rocket: Project Status: Proof of Concept development :rocket:

## Installation

:runner: Coming soon...

## Getting Started

Let's suppose we have the following Product data object:

```java
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class Product {
    @NotNull
    @NotEmpty
    private String id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private List<String> items;
}
```

To create a validator for the product object, declare a validator interface like this:

```java
import org.objectvalidator.Validator;

@Validator
public interface ProductValidator {
    void validate(Prouct product);
}
```

At compile time ObjectValidator will generate an implementation of this interface. The generated implementation uses
plain Java method invocations for validation, i.e. no reflection is involved.

```java
public class ObjectValidatorTester {

    public static void main(String[] args) {
        Product product = new Product();
        ProductValidator validator = new ProductValidatorImpl();
        validator.validate(product);
    }
}
```

See [samples](./samples) for more examples.

## Java Validation API Support Status

### Constraints

|    Constraint     |     Status     |
|-------------------|-----------------------|
|@NotEmpty          | :beer: Supported             |
|@NotNull           | :beer: Supported             |
|@NotBlank          | :beer: Supported             |

### Features

|    Feature     |    Status     |
|----------------|-----------------------|
|Nested validations with @Valid          | :beer: Supported for iterables and arrays           |
|Constraint violation message | :runner: Coming soon...|
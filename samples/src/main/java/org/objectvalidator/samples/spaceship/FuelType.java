package org.objectvalidator.samples.spaceship;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FuelType {

    @NotNull
    private String name;

    private int capacity;
}
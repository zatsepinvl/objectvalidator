package org.objectvalidator.samples.spaceship;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class WarpEngine {

    @NotNull
    private String name;

    @Valid
    @NotNull
    private FuelType fuelType;

    @Valid
    @NotNull
    private Element element;
}
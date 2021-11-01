package org.objectvalidator.samples.spaceship;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Element {
    @NotNull
    private String name;
}
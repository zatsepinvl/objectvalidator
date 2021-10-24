package org.objectvalidator.samples.complex;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WarpEngine {

    @NotNull
    private String name;
}
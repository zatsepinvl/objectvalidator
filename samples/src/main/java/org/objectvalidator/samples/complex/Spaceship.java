package org.objectvalidator.samples.complex;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class Spaceship {
    @NotNull
    @NotEmpty
    private String id;

    @NotNull
    @NotEmpty
    private String name;

    @Valid
    @NotNull
    private WarpEngine engine;

    @Valid
    @NotNull
    private WarpEngine backupEngine;

    @Valid
    @NotNull
    private Element element;
}
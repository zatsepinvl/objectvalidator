package org.objectvalidator.samples.spaceship;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class Spaceship {
    @NotNull
    @NotEmpty
    @NotBlank
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
    private List<Element> element;
}
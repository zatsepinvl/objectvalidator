package org.objectvalidator.samples;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class Product {
    @NotNull
    private String id;

    @NotNull
    @NotEmpty
    private String name;

    private String aliasName;

    @NotEmpty
    @NotNull
    private List<String> items;
}
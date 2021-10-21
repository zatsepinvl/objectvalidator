package org.objectvalidator.samples;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
public class Product {
    @NotNull(message = "Product.id can not be null")
    private String id;

    @NotNull(message = "Product.name can not be null")
    @NotEmpty
    private String name;

    @NotNull(message = "Product.list can not be null")
    @NotEmpty
    private List<String> list;

    @NotNull(message = "Product.map can not be null")
    @NotEmpty
    private Map<String, String> map;

    @NotNull(message = "Product.array can not be null")
    @NotEmpty
    private String[] array;
}
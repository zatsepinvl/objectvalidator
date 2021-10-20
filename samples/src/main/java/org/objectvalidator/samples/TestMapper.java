package org.objectvalidator.samples;

import org.mapstruct.Mapper;

@Mapper
public interface TestMapper {

    Product clone(Product source);
}
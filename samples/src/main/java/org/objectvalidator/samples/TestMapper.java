package org.objectvalidator.samples;

import org.mapstruct.Mapper;
import org.objectvalidator.samples.simple.Car;

@Mapper
public interface TestMapper {

    Car clone(Car source);
}
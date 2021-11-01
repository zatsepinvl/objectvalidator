package org.objectvalidator.samples.spaceship;

import org.mapstruct.Mapper;
import org.objectvalidator.samples.car.Car;

@Mapper
public interface TestMapper {

    Car clone(Car source);
}
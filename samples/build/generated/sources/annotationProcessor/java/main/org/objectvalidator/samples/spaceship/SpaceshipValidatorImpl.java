package org.objectvalidator.samples.spaceship;

import java.lang.Override;
import javax.validation.ConstraintViolationException;

public class SpaceshipValidatorImpl implements SpaceshipValidator {
    @Override
    public void validate(Spaceship spaceship) {
        if (spaceship.getId() == null) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotNull.message}", null);
        }

        if (spaceship.getId().isEmpty()) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotEmpty.message}", null);
        }

        if (spaceship.getId() == null || spaceship.getId().trim().isEmpty()) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotBlank.message}", null);
        }

        if (spaceship.getName() == null) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotNull.message}", null);
        }

        if (spaceship.getName().isEmpty()) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotEmpty.message}", null);
        }

        if (spaceship.getEngine() == null) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotNull.message}", null);
        }

        if (spaceship.getBackupEngine() == null) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotNull.message}", null);
        }

        if (spaceship.getElementsList() == null) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotNull.message}", null);
        }

        validateWarpEngine(spaceship.getEngine());

        validateWarpEngine(spaceship.getBackupEngine());

        for (Element item : spaceship.getElementsList()) {
            validateElement(item);
        }

        for (Element item : spaceship.getElementsArray()) {
            validateElement(item);
        }
    }

    private void validateWarpEngine(WarpEngine warpEngine) {
        if (warpEngine.getName() == null) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotNull.message}", null);
        }

        if (warpEngine.getFuelType() == null) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotNull.message}", null);
        }

        if (warpEngine.getElement() == null) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotNull.message}", null);
        }

        validateFuelType(warpEngine.getFuelType());

        validateElement(warpEngine.getElement());
    }

    private void validateFuelType(FuelType fuelType) {
        if (fuelType.getName() == null) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotNull.message}", null);
        }
    }

    private void validateElement(Element element) {
        if (element.getName() == null) {
            throw new ConstraintViolationException("{javax.validation.constraints.NotNull.message}", null);
        }
    }
}

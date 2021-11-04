package org.objectvalidator.samples.spaceship;

import java.lang.Override;
import javax.validation.ConstraintViolationException;

public class SpaceshipValidatorImpl implements SpaceshipValidator {
    @Override
    public void validate(Spaceship spaceship) {
        if (spaceship.getId() == null) {
            throw new ConstraintViolationException("id must not be null", null);
        }

        if (spaceship.getId().isEmpty()) {
            throw new ConstraintViolationException("id must not be empty", null);
        }

        if (spaceship.getId() == null || spaceship.getId().trim().isEmpty()) {
            throw new ConstraintViolationException("id must not be blank", null);
        }

        if (spaceship.getName() == null) {
            throw new ConstraintViolationException("name must not be null", null);
        }

        if (spaceship.getName().isEmpty()) {
            throw new ConstraintViolationException("name must not be empty", null);
        }

        if (spaceship.getEngine() == null) {
            throw new ConstraintViolationException("engine must not be null", null);
        }

        if (spaceship.getBackupEngine() == null) {
            throw new ConstraintViolationException("backupEngine must not be null", null);
        }

        if (spaceship.getElementsList() == null) {
            throw new ConstraintViolationException("elementsList must not be null", null);
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
            throw new ConstraintViolationException("name must not be null", null);
        }

        if (warpEngine.getFuelType() == null) {
            throw new ConstraintViolationException("fuelType must not be null", null);
        }

        if (warpEngine.getElement() == null) {
            throw new ConstraintViolationException("element must not be null", null);
        }

        validateFuelType(warpEngine.getFuelType());

        validateElement(warpEngine.getElement());
    }

    private void validateFuelType(FuelType fuelType) {
        if (fuelType.getName() == null) {
            throw new ConstraintViolationException("name must not be null", null);
        }
    }

    private void validateElement(Element element) {
        if (element.getName() == null) {
            throw new ConstraintViolationException("name must not be null", null);
        }
    }
}

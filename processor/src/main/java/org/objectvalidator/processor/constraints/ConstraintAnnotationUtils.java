package org.objectvalidator.processor.constraints;

import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.util.Objects;

public final class ConstraintAnnotationUtils {
    @SneakyThrows
    public static String getViolationMessageOrDefault(Annotation annotation, String defaultMessage) {
        String defaultValue = (String) annotation.annotationType().getDeclaredMethod("message").getDefaultValue();
        String currentValue = (String) annotation.annotationType().getDeclaredMethod("message").invoke(annotation);
        if (Objects.equals(defaultValue, currentValue)) {
            return defaultMessage;
        }
        return currentValue;
    }
}
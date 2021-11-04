package org.objectvalidator.processor.constraints;

import com.squareup.javapoet.CodeBlock;
import lombok.SneakyThrows;

import javax.lang.model.element.Element;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotBlank;

public class NotBlankConstraintGenerator implements ConstraintGenerator {

    @Override
    public boolean isSupported(Element element) {
        return element.getAnnotation(NotBlank.class) != null;
    }

    @Override
    @SneakyThrows
    public CodeBlock generate(Element element, String getterMethodName) {
        NotBlank annotation = element.getAnnotation(NotBlank.class);
        String exceptionMessage = ConstraintAnnotationUtils.getViolationMessageOrDefault(
                annotation, element.getSimpleName().toString() + " must not be blank"
        );
        return CodeBlock.builder()
                .beginControlFlow("if ($L == null || $L.trim().isEmpty())", getterMethodName, getterMethodName)
                .addStatement("throw new $T(\"$L\", null)", ConstraintViolationException.class, exceptionMessage)
                .endControlFlow()
                .build();
    }
}
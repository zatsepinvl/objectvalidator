package org.objectvalidator.processor.constraints;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotBlank;

public class NotBlankConstraintGenerator implements ConstraintGenerator {

    @Override
    public boolean isSupported(Element element) {
        return element.getAnnotation(NotBlank.class) != null;
    }

    @Override
    public CodeBlock generate(Element element, String getterMethodName) {
        NotBlank annotation = element.getAnnotation(NotBlank.class);
        String exceptionMessage = annotation.message();
        return CodeBlock.builder()
                .beginControlFlow("if ($L == null || $L.trim().isEmpty())", getterMethodName, getterMethodName)
                .addStatement("throw new $T(\"$L\", null)", ConstraintViolationException.class, exceptionMessage)
                .endControlFlow()
                .build();
    }
}
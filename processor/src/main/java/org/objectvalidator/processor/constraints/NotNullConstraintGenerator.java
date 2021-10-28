package org.objectvalidator.processor.constraints;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

public class NotNullConstraintGenerator implements ConstraintGenerator {

    @Override
    public boolean isSupported(Element element) {
        return element.getAnnotation(NotNull.class) != null;
    }

    @Override
    public CodeBlock generate(Element element, String getterMethodName) {
        NotNull annotation = element.getAnnotation(NotNull.class);
        String exceptionMessage = annotation.message();
        return CodeBlock.builder()
                .beginControlFlow("if ($L == null)", getterMethodName)
                .addStatement("throw new $T(\"$L\", null)", ConstraintViolationException.class, exceptionMessage)
                .endControlFlow()
                .build();
    }
}
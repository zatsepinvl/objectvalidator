package org.objectvalidator.processor.constraints;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;

public interface ConstraintGenerator {
    boolean isSupported(Element element);

    CodeBlock generate(Element element, String getterMethodName);
}
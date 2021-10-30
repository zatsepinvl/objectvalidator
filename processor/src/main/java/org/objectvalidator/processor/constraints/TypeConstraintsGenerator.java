package org.objectvalidator.processor.constraints;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;

public interface TypeConstraintsGenerator {
    CodeBlock generate(String variableName, Element variableType);
}
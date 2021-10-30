package org.objectvalidator.processor;

import javax.lang.model.element.Element;

public interface ValidationCodeGenerator {
    ValidGenerationResult generate(String variableName, Element variableType);
}
package org.objectvalidator.processor;

import javax.lang.model.element.Element;

public interface ValidationCodeGenerator {
    ValidationGenerationResult generate(String variableName, Element variableType);
}
package org.objectvalidator.processor;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;

public interface StatementGenerator {
    boolean isSupported(Element element);

    CodeBlock generate(Element element, String getterMethodName);
}
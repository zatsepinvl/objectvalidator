package org.objectvalidator.processor;

import javax.lang.model.element.Element;

public final class JavaBeans {
    private JavaBeans() {
    }

    public static String getGetterMethodName(Element element) {
        return "get"
                + element.getSimpleName().subSequence(0, 1).toString().toUpperCase()
                + element.getSimpleName().subSequence(1, element.getSimpleName().length());
    }

    public static String getGetterMethod(String variableName, Element propertyType) {
        return variableName + "." + getGetterMethodName(propertyType) + "()";
    }
}
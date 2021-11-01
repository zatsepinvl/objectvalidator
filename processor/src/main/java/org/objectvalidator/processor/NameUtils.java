package org.objectvalidator.processor;

import javax.lang.model.element.Element;

public final class NameUtils {
    private NameUtils() {
    }

    public static String getGetterMethodName(Element element) {
        return "get"
                + element.getSimpleName().subSequence(0, 1).toString().toUpperCase()
                + element.getSimpleName().subSequence(1, element.getSimpleName().length());
    }

    public static String getGetterMethod(String variableName, Element variableClass) {
        return variableName + "." + getGetterMethodName(variableClass) + "()";
    }

    public static String getParameterNameFromElement(Element element) {
        return element.getSimpleName().subSequence(0, 1).toString().toLowerCase()
                + element.getSimpleName().subSequence(1, element.getSimpleName().length());
    }
}
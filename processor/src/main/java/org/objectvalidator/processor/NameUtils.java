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

    public static String getGetterMethod(String variableName, Element propertyType) {
        return variableName + "." + getGetterMethodName(propertyType) + "()";
    }

    public static String getParameterNameFromType(Element parameterType) {
        return parameterType.getSimpleName().subSequence(0, 1).toString().toLowerCase()
                + parameterType.getSimpleName().subSequence(1, parameterType.getSimpleName().length());
    }
}
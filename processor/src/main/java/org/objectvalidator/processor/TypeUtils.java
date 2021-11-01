package org.objectvalidator.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public final class TypeUtils {
    public static boolean isCollectionType(TypeElement typeElement) {
        if (isIterable(typeElement)) {
            return true;
        }

        for (TypeMirror type : typeElement.getInterfaces()) {
            if (isIterable(type)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isIterable(TypeElement element) {
        return isIterable(element.asType());
    }

    private static boolean isIterable(TypeMirror type) {
        return type.toString().startsWith("java.util.Collection")
                || type.toString().startsWith("java.lang.Iterable");
    }

    public static TypeMirror getCollectionItemType(Element field) {
        return ((DeclaredType) field.asType()).getTypeArguments().get(0);
    }
}
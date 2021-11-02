package org.objectvalidator.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public final class TypeUtils {

    public static boolean isCollectionType(Element field, Types types) {
        TypeElement typeElement = (TypeElement) types.asElement(field.asType());
        if (typeElement == null) {
            return false;
        }
        return isCollectionType(typeElement);
    }

    private static boolean isCollectionType(TypeElement typeElement) {
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

    public static boolean isArrayType(Element field) {
        return field.asType().getKind() == TypeKind.ARRAY;
    }

    public static TypeMirror getCollectionItemType(Element field) {
        return ((DeclaredType) field.asType()).getTypeArguments().get(0);
    }

    public static TypeMirror getArrayItemType(Element field) {
        return ((ArrayType) field.asType()).getComponentType();
    }
}
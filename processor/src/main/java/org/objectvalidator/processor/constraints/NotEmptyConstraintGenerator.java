package org.objectvalidator.processor.constraints;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotEmpty;

public class NotEmptyConstraintGenerator implements ConstraintGenerator {

    @Override
    public boolean isSupported(Element element) {
        return element.getAnnotation(NotEmpty.class) != null;
    }

    @Override
    public CodeBlock generate(Element element, String getterMethodName) {
        NotEmpty annotation = element.getAnnotation(NotEmpty.class);
        String ifExpression = "isEmpty()";
        if (element.asType().getKind() == TypeKind.ARRAY) {
            ifExpression = "length == 0";
        }
        return CodeBlock.builder()
                .add(
                        "if ($L." + ifExpression + ") {\n    throw new $T(\"$L\", null);\n}\n",
                        getterMethodName, ConstraintViolationException.class, annotation.message()
                )
                .build();
    }
}
package org.objectvalidator.processor;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotEmpty;

public class NotEmptyStatementGenerator implements StatementGenerator {

    @Override
    public boolean isSupported(Element element) {
        return element.getAnnotation(NotEmpty.class) != null;
    }

    @Override
    public CodeBlock generate(Element element, String objectName) {
        NotEmpty annotation = element.getAnnotation(NotEmpty.class);
        String getter = JavaBeans.getGetterMethodName(element);
        String ifExpression = "$L.$L().isEmpty()";
        if (element.asType().getKind() == TypeKind.ARRAY) {
            ifExpression = "$L.$L().length == 0";
        }
        return CodeBlock.builder()
                .add(
                        "if (" + ifExpression + ") {\n    throw new $T(\"$L\", null);\n}\n",
                        objectName, getter, ConstraintViolationException.class, annotation.message()
                )
                .build();
    }
}
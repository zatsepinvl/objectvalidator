package org.objectvalidator.processor;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

public class NotNullStatementGenerator implements StatementGenerator {

    @Override
    public boolean isSupported(Element element) {
        return element.getAnnotation(NotNull.class) != null;
    }

    @Override
    public CodeBlock generate(Element element, String objectName) {
        NotNull annotation = element.getAnnotation(NotNull.class);
        String getter = JavaBeans.getGetterMethodName(element);
        return CodeBlock.builder()
                .add(
                        "if ($L.$L() == null) {\n    throw new $T(\"$L\", null);\n}\n",
                        objectName, getter, ConstraintViolationException.class, annotation.message()
                )
                .build();
    }
}
package org.objectvalidator.processor;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;


public class ValidatorGenerator {
    public JavaFile generate(Class<?> type) throws IntrospectionException {

        List<CodeBlock> codeBlocks = new ArrayList<>();

        for (Field field : type.getDeclaredFields()) {
            PropertyDescriptor property = new PropertyDescriptor(field.getName(), type);
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation instanceof NotNull) {
                    CodeBlock validationStatement = createNotNullValidationStatement(
                            property.getName(),
                            property.getReadMethod().getName()
                    );
                    codeBlocks.add(validationStatement);
                }
                if (annotation instanceof NotEmpty) {
                    if (field.getType() != String.class && !asList(field.getType().getInterfaces()).contains(Collection.class)) {
                        throw new RuntimeException("Field " + field.getName() + " marked as @NotEmpty and expected to be either String or Collection, but got: " + field.getType());
                    }
                    CodeBlock validationStatement = createNotEmptyValidationStatement(
                            property.getName(),
                            property.getReadMethod().getName()
                    );
                    codeBlocks.add(validationStatement);
                }
            }

        }


        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("validate")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(type, "obj");
        codeBlocks.forEach(methodBuilder::addStatement);
        MethodSpec validateMethod = methodBuilder.build();

        TypeSpec validatorType = TypeSpec.classBuilder(type.getSimpleName() + "Validator")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(validateMethod)
                .build();

        return JavaFile.builder(type.getPackage().getName(), validatorType).build();
    }


    private CodeBlock createNotNullValidationStatement(String property, String getter) {
        return CodeBlock.builder()
                .add(
                        "if(obj.$L() == null) { throw new $T(\"$L can not be null.\"); }",
                        getter, RuntimeException.class, property
                )
                .build();
    }

    private CodeBlock createNotEmptyValidationStatement(String property, String getter) {
        return CodeBlock.builder()
                .add(
                        "if(obj.$L().isEmpty()) { throw new $T(\"$L can not be empty.\"); }",
                        getter, RuntimeException.class, property
                )
                .build();
    }
}
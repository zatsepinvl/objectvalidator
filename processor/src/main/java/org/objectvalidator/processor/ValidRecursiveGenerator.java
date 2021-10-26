package org.objectvalidator.processor;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import org.objectvalidator.processor.constraints.TypeConstraintsGenerator;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Types;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class ValidRecursiveGenerator {

    public static final String VALIDATE_METHOD_PREFIX = "validate";
    private final TypeConstraintsGenerator constraintsGenerator = new TypeConstraintsGenerator();


    public ValidGenerationResult generate(String parentVariableName, Element property, Types types) {

        String propertyVariableName = property.getSimpleName().toString().toLowerCase();
        Element propertyTypeElement = types.asElement(property.asType());
        TypeName propertyTypeName = TypeName.get(property.asType());
        String privateMethodName = VALIDATE_METHOD_PREFIX + propertyTypeElement.getSimpleName();

        CodeBlock validationCode = constraintsGenerator.generate(propertyVariableName, propertyTypeElement);
        MethodSpec validationMethod = MethodSpec.methodBuilder(privateMethodName)
                .addModifiers(Modifier.PRIVATE)
                .addParameter(propertyTypeName, propertyVariableName)
                .addCode(validationCode)
                .build();
        CodeBlock validationInvocationCode = CodeBlock.builder()
                .add(
                        "\n$L($L);\n",
                        privateMethodName, JavaBeans.getGetterMethod(parentVariableName, property)
                )
                .build();

        List<MethodSpec> privateMethods = new ArrayList<>();
        for (Element element : propertyTypeElement.getEnclosedElements()) {
            if (element.getKind() != ElementKind.FIELD) continue;
            if (element.getAnnotation(Valid.class) != null) {
                ValidGenerationResult result = generate(propertyVariableName, element, types);
                privateMethods.addAll(result.getPrivateMethods());
                validationMethod = validationMethod.toBuilder()
                        .addCode(result.getValidationInvocationCode())
                        .build();
            }
        }

        privateMethods.add(0, validationMethod);

        return new ValidGenerationResult(validationInvocationCode, privateMethods);
    }

}
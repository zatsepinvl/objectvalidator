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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

public class ValidRecursiveGenerator {

    public static final String VALIDATE_METHOD_PREFIX = "validate";
    private final TypeConstraintsGenerator constraintsGenerator = new TypeConstraintsGenerator();


    // ToDo: This is quite creepy algorithm - should be refactored
    public ValidGenerationResult generate(String parentVariableName, Element parent, Types types) {
        List<MethodSpec> privateMethods = new ArrayList<>();
        List<CodeBlock> invocationCodes = new ArrayList<>();
        Set<String> methodNames = new HashSet<>();

        for (Element property : parent.getEnclosedElements()) {
            if (property.getKind() != ElementKind.FIELD) continue;
            if (property.getAnnotation(Valid.class) != null) {
                ValidGenerationResult generationResult = generateForProperty(parentVariableName, property, types);
                generationResult.getPrivateMethods().stream()
                        .filter(method -> !methodNames.contains(method.name))
                        .forEach(method -> {
                            privateMethods.add(method);
                            methodNames.add(method.name);
                        });
                invocationCodes.add(generationResult.getValidationInvocationCode());
            }
        }

        CodeBlock validationInvocationCode = CodeBlock.join(invocationCodes, "");
        return new ValidGenerationResult(validationInvocationCode, privateMethods);
    }

    private ValidGenerationResult generateForProperty(String parentVariableName, Element property, Types types) {
        Element propertyTypeElement = types.asElement(property.asType());
        TypeName propertyTypeName = TypeName.get(property.asType());
        String privateMethodName = VALIDATE_METHOD_PREFIX + propertyTypeElement.getSimpleName();
        String propertyVariableName = NameUtils.getParameterNameFromType(propertyTypeElement);

        CodeBlock validationCode = constraintsGenerator.generate(propertyVariableName, propertyTypeElement);
        String propertyGetter = NameUtils.getGetterMethod(parentVariableName, property);
        CodeBlock validationInvocationCode = CodeBlock.builder()
                .addStatement("$L($L)", privateMethodName, propertyGetter)
                .build();

        ValidGenerationResult result = generate(propertyVariableName, propertyTypeElement, types);
        List<MethodSpec> privateMethods = new ArrayList<>(result.getPrivateMethods());
        validationCode = CodeBlock.join(asList(validationCode, result.getValidationInvocationCode()), "\n");

        MethodSpec validationMethod = MethodSpec.methodBuilder(privateMethodName)
                .addModifiers(Modifier.PRIVATE)
                .addParameter(propertyTypeName, propertyVariableName)
                .addCode(validationCode)
                .build();
        privateMethods.add(0, validationMethod);

        return new ValidGenerationResult(validationInvocationCode, privateMethods);
    }

}
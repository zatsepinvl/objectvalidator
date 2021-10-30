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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ValidationCodeGeneratorImpl implements ValidationCodeGenerator {

    public static final String VALIDATE_METHOD_PREFIX = "validate";

    private final Types types;
    private final TypeConstraintsGenerator constraintsGenerator;

    public ValidationCodeGeneratorImpl(Types types, TypeConstraintsGenerator constraintsGenerator) {
        this.types = types;
        this.constraintsGenerator = constraintsGenerator;
    }

    @Override
    public ValidGenerationResult generate(String variableName, Element variableType) {
        CodeBlock invocationCode = generateValidationCode(variableName, variableType);
        Map<String, MethodSpec> methods = new HashMap<>();

        visitValidProperties(variableType, property -> {
            Element propertyType = types.asElement(property.asType());
            TypeName propertyTypeName = TypeName.get(property.asType());
            String propertyVariableName = NameUtils.getParameterNameFromType(propertyType);

            ValidGenerationResult generationResult = generate(propertyVariableName, propertyType);

            String privateMethodName = getValidationMethodName(propertyType);
            MethodSpec validationMethod = MethodSpec.methodBuilder(privateMethodName)
                    .addModifiers(Modifier.PRIVATE)
                    .addParameter(propertyTypeName, propertyVariableName)
                    .addCode(generationResult.getValidationCode())
                    .build();

            methods.putIfAbsent(validationMethod.name, validationMethod);
            generationResult.getPrivateMethods().forEach(method -> methods.putIfAbsent(method.name, method));
        });

        return new ValidGenerationResult(invocationCode, methods.values());
    }

    private CodeBlock generateValidationCode(String variableName, Element variableType) {
        CodeBlock validationCode = constraintsGenerator.generate(variableName, variableType);
        List<CodeBlock> validationCodes = new ArrayList<>();
        validationCodes.add(validationCode);
        visitValidProperties(variableType, property -> {
            Element propertyType = types.asElement(property.asType());
            String propertyGetter = NameUtils.getGetterMethod(variableName, property);
            String privateMethodName = getValidationMethodName(propertyType);
            CodeBlock nestedValidationInvocation = CodeBlock.builder()
                    .addStatement("$L($L)", privateMethodName, propertyGetter)
                    .build();
            validationCodes.add(nestedValidationInvocation);
        });
        return CodeBlock.join(validationCodes, Formatting.CODE_SEPARATOR);
    }

    private String getValidationMethodName(Element propertyType) {
        return VALIDATE_METHOD_PREFIX + propertyType.getSimpleName();
    }

    private void visitValidProperties(Element element, Consumer<Element> propertyConsumer) {
        for (Element property : element.getEnclosedElements()) {
            if (property.getKind() != ElementKind.FIELD) continue;
            if (property.getAnnotation(Valid.class) != null) {
                propertyConsumer.accept(property);
            }
        }
    }
}
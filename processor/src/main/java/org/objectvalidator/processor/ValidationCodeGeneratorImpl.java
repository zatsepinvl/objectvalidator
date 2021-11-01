package org.objectvalidator.processor;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import org.objectvalidator.processor.constraints.TypeConstraintsGenerator;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
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
    public ValidationGenerationResult generate(String variableName, Element variableType) {
        CodeBlock invocationCode = generateValidationCode(variableName, variableType);
        Map<String, MethodSpec> methods = new HashMap<>();

        visitValidProperties(variableType, field -> {
            Element fieldClass = types.asElement(field.asType());
            boolean isCollectionType = TypeUtils.isCollectionType((TypeElement) fieldClass);

            ValidationGenerationResult generationResult;
            MethodSpec validationMethod;

            // ToDo: remove duplication
            if (isCollectionType) {
                TypeMirror itemClassType = TypeUtils.getCollectionItemType(field);
                Element itemClassElement = types.asElement(itemClassType);
                String privateMethodName = getValidationMethodName(itemClassElement);
                String itemVariableName = NameUtils.getParameterNameFromElement(itemClassElement);
                generationResult = generate(itemVariableName, itemClassElement);
                validationMethod = MethodSpec.methodBuilder(privateMethodName)
                        .addModifiers(Modifier.PRIVATE)
                        .addParameter(TypeName.get(itemClassType), itemVariableName)
                        .addCode(generationResult.getValidationCode())
                        .build();
            } else {
                String propertyVariableName = NameUtils.getParameterNameFromElement(fieldClass);
                TypeName propertyTypeName = TypeName.get(field.asType());
                generationResult = generate(propertyVariableName, fieldClass);
                String privateMethodName = getValidationMethodName(fieldClass);
                validationMethod = MethodSpec.methodBuilder(privateMethodName)
                        .addModifiers(Modifier.PRIVATE)
                        .addParameter(propertyTypeName, propertyVariableName)
                        .addCode(generationResult.getValidationCode())
                        .build();
            }

            methods.putIfAbsent(validationMethod.name, validationMethod);
            generationResult.getPrivateMethods().forEach(method -> methods.putIfAbsent(method.name, method));
        });

        return new ValidationGenerationResult(invocationCode, methods.values());
    }

    private CodeBlock generateValidationCode(String variableName, Element variableType) {
        CodeBlock validationCode = constraintsGenerator.generate(variableName, variableType);
        List<CodeBlock> validationCodes = new ArrayList<>();
        validationCodes.add(validationCode);
        visitValidProperties(variableType, field -> {
            Element fieldClass = types.asElement(field.asType());
            boolean isCollectionType = TypeUtils.isCollectionType((TypeElement) fieldClass);
            String propertyGetter = NameUtils.getGetterMethod(variableName, field);
            CodeBlock nestedValidationInvocation;
            if (isCollectionType) {
                Element itemClassElement = types.asElement(TypeUtils.getCollectionItemType(field));
                Name itemClassName = itemClassElement.getSimpleName();
                String privateMethodName = getValidationMethodName(itemClassElement);
                nestedValidationInvocation = CodeBlock.builder()
                        .beginControlFlow("for ($L item : $L)", itemClassName, propertyGetter)
                        .addStatement("$L(item)", privateMethodName)
                        .endControlFlow()
                        .build();
            } else {
                String privateMethodName = getValidationMethodName(fieldClass);
                nestedValidationInvocation = CodeBlock.builder()
                        .addStatement("$L($L)", privateMethodName, propertyGetter)
                        .build();
            }
            validationCodes.add(nestedValidationInvocation);
        });
        return CodeBlock.join(validationCodes, Formatting.CODE_SEPARATOR);
    }

    private String getValidationMethodName(Element classElement) {
        return VALIDATE_METHOD_PREFIX + classElement.getSimpleName();
    }

    private void visitValidProperties(Element classElement, Consumer<Element> fieldConsumer) {
        for (Element element : classElement.getEnclosedElements()) {
            if (element.getKind() != ElementKind.FIELD) continue;
            if (element.getAnnotation(Valid.class) != null) {
                fieldConsumer.accept(element);
            }
        }
    }
}
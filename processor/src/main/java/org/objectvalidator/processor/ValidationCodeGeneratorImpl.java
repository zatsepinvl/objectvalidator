package org.objectvalidator.processor;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import org.objectvalidator.processor.constraints.TypeConstraintsGenerator;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
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
    public ValidationGenerationResult generate(String propertyName, Element propertyClass) {
        CodeBlock validationCode = generateValidationCode(propertyName, propertyClass);
        Map<String, MethodSpec> methods = new HashMap<>();

        visitValidFields(propertyClass, field -> {
            boolean isCollectionType = TypeUtils.isCollectionTypeFromField(field, types);
            Element nestedFieldClass;
            if (isCollectionType) {
                TypeMirror itemClassType = TypeUtils.getCollectionItemType(field);
                nestedFieldClass = types.asElement(itemClassType);
            } else {
                nestedFieldClass = types.asElement(field.asType());
            }

            String nestedFieldVariableName = NameUtils.getVariableNameForClass(nestedFieldClass);
            ValidationGenerationResult generationResult = generate(nestedFieldVariableName, nestedFieldClass);
            MethodSpec validationMethod = generateValidationMethodForClass(nestedFieldClass, generationResult.getValidationCode());

            methods.putIfAbsent(validationMethod.name, validationMethod);
            generationResult.getPrivateMethods().forEach(method -> methods.putIfAbsent(method.name, method));
        });

        return new ValidationGenerationResult(validationCode, methods.values());
    }

    private MethodSpec generateValidationMethodForClass(Element classElement, CodeBlock validationCode) {
        String parameterName = NameUtils.getVariableNameForClass(classElement);
        TypeName parameterTypeName = TypeName.get(classElement.asType());
        String privateMethodName = getValidationMethodName(classElement);
        return MethodSpec.methodBuilder(privateMethodName)
                .addModifiers(Modifier.PRIVATE)
                .addParameter(parameterTypeName, parameterName)
                .addCode(validationCode)
                .build();
    }

    private CodeBlock generateValidationCode(String variableName, Element propertyClass) {
        CodeBlock validationCode = constraintsGenerator.generate(variableName, propertyClass);
        List<CodeBlock> validationCodes = new ArrayList<>();
        validationCodes.add(validationCode);
        visitValidFields(propertyClass, field -> {
            CodeBlock nestedValidationInvocation;
            String fieldGetterExpression = NameUtils.getFieldGetterExpression(variableName, field);
            boolean isCollectionType = TypeUtils.isCollectionTypeFromField(field, types);
            if (isCollectionType) {
                nestedValidationInvocation = generateValidationInvocationCodeForIterableField(field, fieldGetterExpression);
            } else {
                nestedValidationInvocation = generateValidationInvocationCodeForField(field, fieldGetterExpression);
            }
            validationCodes.add(nestedValidationInvocation);
        });
        return CodeBlock.join(validationCodes, Formatting.CODE_SEPARATOR);
    }

    private void visitValidFields(Element classElement, Consumer<Element> fieldConsumer) {
        for (Element element : classElement.getEnclosedElements()) {
            if (element.getKind() != ElementKind.FIELD) continue;
            if (element.getAnnotation(Valid.class) != null) {
                fieldConsumer.accept(element);
            }
        }
    }

    private CodeBlock generateValidationInvocationCodeForIterableField(Element field, String fieldGetterExpression) {
        TypeMirror itemType = TypeUtils.getCollectionItemType(field);
        Element itemClassElement = types.asElement(itemType);
        Name itemClassName = itemClassElement.getSimpleName();
        String privateMethodName = getValidationMethodName(itemClassElement);
        return CodeBlock.builder()
                .beginControlFlow("for ($L item : $L)", itemClassName, fieldGetterExpression)
                .addStatement("$L(item)", privateMethodName)
                .endControlFlow()
                .build();
    }

    private CodeBlock generateValidationInvocationCodeForField(Element field, String fieldGetterExpression) {
        Element fieldClass = types.asElement(field.asType());
        String privateMethodName = getValidationMethodName(fieldClass);
        return CodeBlock.builder()
                .addStatement("$L($L)", privateMethodName, fieldGetterExpression)
                .build();
    }

    private String getValidationMethodName(Element classElement) {
        return VALIDATE_METHOD_PREFIX + classElement.getSimpleName();
    }
}
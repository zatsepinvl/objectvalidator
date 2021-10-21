package org.objectvalidator.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

@SupportedAnnotationTypes("org.objectvalidator.Validator")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ValidatorProcessor extends AbstractProcessor {

    private static final String VALIDATOR_IMPL_SUFFIX = "Impl";
    private static final String DEFAULT_INDENT = "    "; // 4 spaces

    private final List<StatementGenerator> statementGenerators = Arrays.asList(
            new NotNullStatementGenerator(),
            new NotEmptyStatementGenerator()
    );

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : annotatedElements) {
                if (element instanceof TypeElement) {
                    processElement((TypeElement) element);
                }
            }
        }
        return true;
    }

    private void processElement(TypeElement element) {
        List<MethodSpec> methods = new ArrayList<>();
        for (Element enclosedElement : element.getEnclosedElements()) {
            if (enclosedElement instanceof ExecutableElement) {
                MethodSpec method = createMethod((ExecutableElement) enclosedElement);
                methods.add(method);
            }
        }

        PackageElement packageOf = processingEnv.getElementUtils().getPackageOf(element);
        String validatorClassName = element.getSimpleName() + VALIDATOR_IMPL_SUFFIX;
        TypeSpec validatorType = createValidatorImpl(validatorClassName, methods, element.asType());
        JavaFile javaFile = JavaFile.builder(packageOf.toString(), validatorType)
                .indent(DEFAULT_INDENT)
                .build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TypeSpec createValidatorImpl(String className, Iterable<MethodSpec> methods, TypeMirror superinterface) {
        return TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addMethods(methods)
                .addSuperinterface(superinterface)
                .build();
    }

    private MethodSpec createMethod(ExecutableElement element) {
        if (element.getParameters().size() != 1) {
            String message = format(
                    "Method \"%s\" declared in the mapper interface should have exactly one parameter, but got %d.",
                    element.getSimpleName(), element.getParameters().size()
            );
            throw new RuntimeException(message);
        }

        VariableElement param = element.getParameters().get(0);
        String paramName = param.getSimpleName().toString();

        List<CodeBlock> validationStatements = new ArrayList<>();
        for (Element property : processingEnv.getTypeUtils().asElement(param.asType()).getEnclosedElements()) {
            if (property.getKind() != ElementKind.FIELD) continue;
            statementGenerators.stream()
                    .filter(generator -> generator.isSupported(property))
                    .map(generator -> generator.generate(property, paramName))
                    .forEach(validationStatements::add);
        }

        MethodSpec.Builder builder = MethodSpec.methodBuilder(element.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        TypeName.get(param.asType()),
                        param.getSimpleName().toString()
                );

        validationStatements.forEach(builder::addCode);
        return builder.build();
    }
}
package org.objectvalidator.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("org.objectvalidator.Validator")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ValidatorProcessor extends AbstractProcessor {

    private static final String VALIDATOR_IMPL_SUFFIX = "Impl";

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
                .indent("    ") // 4 spaces
                .build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
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
        VariableElement param = element.getParameters().get(0);

        String paramName = param.getSimpleName().toString();
        List<CodeBlock> validationStatements = new ArrayList<>();
        for (Element property : processingEnv.getTypeUtils().asElement(param.asType()).getEnclosedElements()) {
            if (property.getKind() != ElementKind.FIELD) continue;
            for (AnnotationMirror annotationMirror : processingEnv.getElementUtils().getAllAnnotationMirrors(property)) {
                String getter = "get" + property.getSimpleName().subSequence(0, 1).toString().toUpperCase() + property.getSimpleName().subSequence(1, property.getSimpleName().length());
                if (annotationMirror.getAnnotationType().toString().equals(NotEmpty.class.getCanonicalName())) {
                    CodeBlock notNullValidationStatement = CodeBlock.builder()
                            .add(
                                    "if ($L.$L().isEmpty()) {\n    throw new $T(\"$L can not be empty.\");\n}\n",
                                    paramName, getter, RuntimeException.class, property
                            )
                            .build();
                    validationStatements.add(notNullValidationStatement);
                }
                if (annotationMirror.getAnnotationType().toString().equals(NotNull.class.getCanonicalName())) {
                    CodeBlock notNullValidationStatement = CodeBlock.builder()
                            .add(
                                    "if ($L.$L() == null) {\n    throw new $T(\"$L can not be null.\");\n}\n",
                                    paramName, getter, RuntimeException.class, property
                            )
                            .build();
                    validationStatements.add(notNullValidationStatement);
                }
            }
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
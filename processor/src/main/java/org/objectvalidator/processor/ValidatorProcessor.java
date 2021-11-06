package org.objectvalidator.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.objectvalidator.ComponentModel;
import org.objectvalidator.Validator;
import org.objectvalidator.processor.constraints.TypeConstraintsGeneratorImpl;
import org.springframework.stereotype.Service;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

@SupportedAnnotationTypes("org.objectvalidator.Validator")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ValidatorProcessor extends AbstractProcessor {

    private static final String VALIDATOR_IMPL_SUFFIX = "Impl";

    private ValidationCodeGeneratorImpl validationCodeGenerator;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        validationCodeGenerator = new ValidationCodeGeneratorImpl(
                processingEnv.getTypeUtils(),
                new TypeConstraintsGeneratorImpl()
        );
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : annotatedElements) {
                if (element instanceof TypeElement) {
                    generateValidatorImpl((TypeElement) element);
                }
            }
        }
        return true;
    }

    private void generateValidatorImpl(TypeElement element) {
        List<MethodSpec> methods = new ArrayList<>();

        for (Element enclosedElement : element.getEnclosedElements()) {
            if (enclosedElement instanceof ExecutableElement) {
                List<MethodSpec> method = generateValidatorImplMethods((ExecutableElement) enclosedElement);
                methods.addAll(method);
            }
        }

        TypeSpec validatorTypeSpec = generateValidatorImplClassType(element, methods);
        writeValidatorImplJavaFile(element, validatorTypeSpec);
    }

    private List<MethodSpec> generateValidatorImplMethods(ExecutableElement element) {
        if (element.getParameters().size() != 1) {
            String message = format(
                    "Method \"%s\" declared in the mapper interface should have exactly one parameter, but got %d.",
                    element.getSimpleName(), element.getParameters().size()
            );
            throw new RuntimeException(message);
        }

        VariableElement param = element.getParameters().get(0);
        Element paramType = processingEnv.getTypeUtils().asElement(param.asType());
        String paramName = param.getSimpleName().toString();

        ValidationGenerationResult generationResult = validationCodeGenerator.generate(paramName, paramType);

        MethodSpec publicMethod = MethodSpec.methodBuilder(element.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        TypeName.get(param.asType()),
                        paramName
                )
                .addCode(generationResult.getValidationCode())
                .build();

        List<MethodSpec> methods = new ArrayList<>();
        methods.add(publicMethod);
        methods.addAll(generationResult.getPrivateMethods());
        return methods;
    }

    private TypeSpec generateValidatorImplClassType(Element interfaceElement, Iterable<MethodSpec> methods) {
        String validatorClassName = interfaceElement.getSimpleName() + VALIDATOR_IMPL_SUFFIX;
        TypeSpec.Builder builder = TypeSpec.classBuilder(validatorClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethods(methods)
                .addSuperinterface(interfaceElement.asType());
        if (interfaceElement.getAnnotation(Validator.class).componentModel() == ComponentModel.SPRING) {
            builder = builder.addAnnotation(Service.class);
        }
        return builder.build();
    }

    private void writeValidatorImplJavaFile(TypeElement interfaceClass, TypeSpec validatorTypeSpec) {
        PackageElement packageOf = processingEnv.getElementUtils().getPackageOf(interfaceClass);
        JavaFile javaFile = JavaFile.builder(packageOf.toString(), validatorTypeSpec)
                .indent(Formatting.INDENT)
                .build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
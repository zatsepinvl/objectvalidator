package org.objectvalidator.processor.constraints;

import com.squareup.javapoet.CodeBlock;
import org.objectvalidator.processor.Formatting;
import org.objectvalidator.processor.NameUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class TypeConstraintsGeneratorImpl implements TypeConstraintsGenerator {

    private final List<ConstraintGenerator> statementGenerators = asList(
            new NotNullConstraintGenerator(),
            new NotEmptyConstraintGenerator()
    );

    @Override
    public CodeBlock generate(String variableName, Element variableType) {
        List<CodeBlock> validationStatements = new ArrayList<>();
        for (Element property : variableType.getEnclosedElements()) {
            if (property.getKind() != ElementKind.FIELD) continue;
            String getterMethod = NameUtils.getGetterMethod(variableName, property);
            statementGenerators.stream()
                    .filter(generator -> generator.isSupported(property))
                    .map(generator -> generator.generate(property, getterMethod))
                    .forEach(validationStatements::add);
        }
        return CodeBlock.join(validationStatements, Formatting.CODE_SEPARATOR);
    }
}
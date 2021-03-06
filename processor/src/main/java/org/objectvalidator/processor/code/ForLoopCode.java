package org.objectvalidator.processor.code;

import com.squareup.javapoet.CodeBlock;
import lombok.Builder;
import lombok.Value;

import javax.lang.model.element.Name;

@Value
@Builder
public class ForLoopCode {
    Name collectionClass;
    String variableName;
    String in;
    CodeBlock body;

    public CodeBlock toCodeBlock() {
        return CodeBlock.builder()
                .beginControlFlow("for ($L $L : $L)", collectionClass, variableName, in)
                .add(body)
                .endControlFlow()
                .build();
    }
}
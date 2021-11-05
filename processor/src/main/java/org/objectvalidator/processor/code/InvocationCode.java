package org.objectvalidator.processor.code;

import com.squareup.javapoet.CodeBlock;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(builderMethodName = "generator", buildMethodName = "prepare")
public class InvocationCode {
    String method;
    String variable;

    public CodeBlock generate() {
        return CodeBlock.builder()
                .addStatement("$L($L)", method, variable)
                .build();
    }
}
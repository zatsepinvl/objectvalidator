package org.objectvalidator.processor;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidGenerationResult {
    private CodeBlock validationInvocationCode;
    private List<MethodSpec> privateMethods;
}
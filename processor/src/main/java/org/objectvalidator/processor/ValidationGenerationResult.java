package org.objectvalidator.processor;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationGenerationResult {
    private CodeBlock validationCode;
    private Collection<MethodSpec> privateMethods;
}
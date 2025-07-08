
package com.sysml.lightmodel.semantic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Contextual use of a definition
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Usage extends Element {

    private String definitionName; // DSL 层：引用名，如 "Real", "Controller"

    private String type;           // PartUsage, AttributeUsage, ConstraintUsage 等
    private String multiplicity;
    private String defaultValue;

    @JsonIgnore
    private Definition parentDefinition;

    private Definition resolvedDefinition; // ✅ 语义层：已解析的真实定义对象
}


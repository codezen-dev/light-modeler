
package com.sysml.lightmodel.semantic;

import lombok.Data;

/**
 * Use of a constraint in context
 */
@Data
public class ConstraintUsage extends Usage {
    private String expression;

}
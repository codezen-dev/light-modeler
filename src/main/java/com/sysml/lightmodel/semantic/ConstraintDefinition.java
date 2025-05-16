
package com.sysml.lightmodel.semantic;

import lombok.Data;
import java.util.List;

/**
 * Defines a constraint expression
 */
@Data
public class ConstraintDefinition extends Definition {
    
    private Expression expression;
    
}
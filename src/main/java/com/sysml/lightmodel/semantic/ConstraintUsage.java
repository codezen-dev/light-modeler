
package com.sysml.lightmodel.semantic;

import lombok.Data;
import java.util.List;

/**
 * Use of a constraint in context
 */
@Data
public class ConstraintUsage extends Usage {
    
    private ConstraintDefinition definition;
    
}

package com.sysml.lightmodel.semantic;

import lombok.Data;
import java.util.List;

/**
 * Literal value expression
 */
@Data
public class ValueExpression extends Expression {
    
    private String value;
    
}
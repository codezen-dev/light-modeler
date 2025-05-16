
package com.sysml.lightmodel.semantic;

import lombok.Data;
import java.util.List;

/**
 * Boolean logic expression
 */
@Data
public class BooleanExpression extends Expression {
    
    private String operator;
    
    private List<Expression> operands;
    
}
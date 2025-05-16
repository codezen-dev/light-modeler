
package com.sysml.lightmodel.semantic;

import lombok.Data;
import java.util.List;

/**
 * Use of a part within a structure
 */
@Data
public class PartUsage extends Usage {
    
    private StructureDefinition definition;
    
    private String multiplicity;
    
}
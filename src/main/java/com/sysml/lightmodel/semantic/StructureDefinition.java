
package com.sysml.lightmodel.semantic;

import lombok.Data;
import java.util.List;

/**
 * Defines the structure of a component or element
 */
@Data
public class StructureDefinition extends Definition {
    
    private List<Feature> features;
    
    private List<Definition> specializes;
    
}

package com.sysml.lightmodel.semantic;

import lombok.Data;
import java.util.List;

/**
 * Element feature (e.g. port, parameter)
 */
@Data
public class Feature extends Element {
    
    private String direction;
    
    private String featureType;
    
}
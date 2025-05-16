package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.Element;

public class ValueDefinitionDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        return MetaDslFormatter.renderValueDefinition(element, indent);
    }
}

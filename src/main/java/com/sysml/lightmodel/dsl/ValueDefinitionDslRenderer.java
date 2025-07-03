package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;

import java.util.Map;

public class ValueDefinitionDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        String indentStr = DslRenderUtils.indent(indent);
        Map<String, Object> meta = element.getMetadata();

        return indentStr + "ValueDefinition \"" + element.getName() + "\""
                + (meta != null && meta.get("value") != null ? " = " + meta.get("value") : "")
                + MetaDslFormatter.formatModifiers(element.getModifiers())
                + "\n";
    }
}


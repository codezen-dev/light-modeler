package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;

public class StructureDefinitionDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        String indentStr = DslRenderUtils.indent(indent);
        StringBuilder builder = new StringBuilder();

        builder.append(indentStr)
                .append("StructureDefinition \"").append(element.getName()).append("\"")
                .append(MetaDslFormatter.formatModifiers(element.getModifiers()))
                .append(" {\n");

        for (Element child : element.getChildren()) {
            builder.append(DslRendererRegistry.getRenderer(child.getType()).render(child, indent + 1));
        }

        builder.append(indentStr).append("}\n");
        return builder.toString();
    }
}


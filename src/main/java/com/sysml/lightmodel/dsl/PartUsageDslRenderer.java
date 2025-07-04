package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;

import java.util.Map;

public class PartUsageDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        StringBuilder builder = new StringBuilder();
        String indentStr = DslRenderUtils.indent(indent);
        Map<String, Object> meta = element.getMetadata();
        DslRenderUtils.appendDocumentation(builder, element, indentStr);
        builder.append(indentStr)
                .append("PartUsage \"").append(element.getName()).append("\"")
                .append(DslRenderUtils.resolveDefinition(meta))
                .append(MetaDslFormatter.formatDirectionAndModifiers(meta, element.getModifiers()))
                .append(" {\n");

        for (Element child : element.getChildren()) {
            builder.append(DslRendererRegistry.getRenderer(child.getType()).render(child, indent + 1));
        }
        builder.append(indentStr).append("}\n");

        return builder.toString();
    }
}


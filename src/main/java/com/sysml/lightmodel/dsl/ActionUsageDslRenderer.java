package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;

import java.util.Map;

public class ActionUsageDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        String indentStr = DslRenderUtils.indent(indent);
        StringBuilder builder = new StringBuilder();
        Map<String, Object> meta = element.getMetadata();

        builder.append(indentStr)
                .append("ActionUsage \"").append(element.getName()).append("\"")
                .append(MetaDslFormatter.formatDefinition(meta))
                .append(MetaDslFormatter.formatDirectionAndModifiers(meta, element.getModifiers()))
                .append(" {\n");

        for (Element child : element.getChildren()) {
            builder.append(DslRendererRegistry.getRenderer(child.getType()).render(child, indent + 1));
        }

        builder.append(indentStr).append("}\n");
        return builder.toString();
    }
}


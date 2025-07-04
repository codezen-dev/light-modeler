package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;

import java.util.Map;

public class AttributeUsageDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        String indentStr = DslRenderUtils.indent(indent);
        StringBuilder builder = new StringBuilder();
        Map<String, Object> meta = element.getMetadata();

        DslRenderUtils.appendDocumentation(builder, element, indentStr);

        builder.append(indentStr)
                .append("AttributeUsage \"").append(element.getName()).append("\"")
                .append(DslRenderUtils.resolveType(meta))
                .append(MetaDslFormatter.formatDefaultValue(meta))
                .append(MetaDslFormatter.formatMultiplicity(meta))
                .append(MetaDslFormatter.formatDirectionAndModifiers(meta, element.getModifiers()))
                .append("\n");

        return builder.toString();
    }
}


package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;

public class ActionUsageDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        StringBuilder sb = new StringBuilder();
        String indentStr = DslRenderUtils.indent(indent);
        DslRenderUtils.appendDocumentation(sb, element, indentStr);

        String typeStr = element.getDefinitionName();
        if (typeStr == null && element.getResolvedDefinition() != null) {
            typeStr = element.getResolvedDefinition().getName();
        }
        if (typeStr == null) {
            typeStr = "null";
        }

        sb.append(indentStr)
                .append("action ").append(element.getName())
                .append(": ").append(typeStr)
                .append(DslRenderHelper.renderMultiplicity(element))
                .append(DslRenderHelper.renderMetadata(element))
                .append(DslRenderHelper.renderDocumentation(element))
                .append("\n");
        return sb.toString();
    }
}


package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;

public class PartUsageDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        StringBuilder sb = new StringBuilder();
        String indentStr = DslRenderUtils.indent(indent);
        DslRenderUtils.appendDocumentation(sb, element, indentStr);
        sb.append(indentStr)
                .append("part ").append(element.getName())
                .append(": ").append(element.getDefinitionName())
                .append(DslRenderHelper.renderMultiplicity(element))
                .append(DslRenderHelper.renderMetadata(element))
                .append(DslRenderHelper.renderDocumentation(element))
                .append("\n");
        return sb.toString();
    }
}


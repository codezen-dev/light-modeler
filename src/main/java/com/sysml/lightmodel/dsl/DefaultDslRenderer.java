package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.semantic.DslRenderUtils;

public class DefaultDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        StringBuilder builder = new StringBuilder();
        String indentStr = DslRenderUtils.indent(indent);

        builder.append(indentStr)
                .append(element.getType())
                .append(" \"").append(element.getName()).append("\" {")
                .append("\n");

        for (Element child : element.getChildren()) {
            builder.append(DslRendererRegistry.getRenderer(child.getType()).render(child, indent + 1));
        }
        builder.append(indentStr).append("}\n");
        return builder.toString();
    }
}

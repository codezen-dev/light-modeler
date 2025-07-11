package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;

public class StructureDefinitionDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        StringBuilder sb = new StringBuilder();
        String indentStr = DslRenderUtils.indent(indent);

        DslRenderUtils.appendDocumentation(sb, element, indentStr);
        sb.append(indentStr).append("def StructureDefinition ").append(element.getName()).append(" {\n");

        if (element.getChildren() != null) {
            for (Element child : element.getChildren()) {
                String rendered = DslRendererRegistry.render(child, indent + 1);
                sb.append(rendered);
            }
        }

        sb.append(indentStr).append("}\n");
        return sb.toString();
    }
}


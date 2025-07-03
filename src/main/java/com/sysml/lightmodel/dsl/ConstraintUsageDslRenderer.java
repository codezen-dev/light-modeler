package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;

import java.util.Map;

import static com.sysml.lightmodel.dsl.MetaDslFormatter.getString;

public class ConstraintUsageDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        String indentStr = DslRenderUtils.indent(indent);
        StringBuilder builder = new StringBuilder();
        Map<String, Object> meta = element.getMetadata();

        builder.append(indentStr)
                .append("ConstraintUsage \"").append(element.getName()).append("\"")
                .append(MetaDslFormatter.formatDirectionAndModifiers(meta, element.getModifiers()));

        return getString(element, indent, indentStr, builder, meta);
    }


}


package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;

import java.util.Map;

public class AttributeUsageDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        String indentStr = DslRenderUtils.indent(indent);
        Map<String, Object> meta = element.getMetadata();

        return indentStr + "AttributeUsage \"" + element.getName() + "\""
                + MetaDslFormatter.formatType(meta)
                + MetaDslFormatter.formatDefaultValue(meta)
                + MetaDslFormatter.formatDirectionAndModifiers(meta, element.getModifiers())
                + "\n";
    }
}


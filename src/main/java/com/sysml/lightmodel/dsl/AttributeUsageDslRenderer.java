package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;

import java.util.Map;

public class AttributeUsageDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        return MetaDslFormatter.renderAttributeUsage(element, indent);
    }
}

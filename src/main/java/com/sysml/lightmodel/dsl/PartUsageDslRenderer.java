package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.Element;

import java.util.Map;

public class PartUsageDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        return MetaDslFormatter.renderPartUsage(element, indent);
    }
}

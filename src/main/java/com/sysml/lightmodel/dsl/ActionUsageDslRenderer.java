package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.Element;

public class ActionUsageDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        return MetaDslFormatter.renderActionUsage(element, indent);
    }
}

package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;

public class ConstraintUsageDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        return MetaDslFormatter.renderConstraintUsage(element, indent);
    }
}

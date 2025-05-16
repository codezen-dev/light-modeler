package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.Element;

public interface DslRenderer {
    String render(Element element, int indent);
}

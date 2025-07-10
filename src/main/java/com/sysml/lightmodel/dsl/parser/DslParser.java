package com.sysml.lightmodel.dsl.parser;

import com.sysml.lightmodel.semantic.Element;

public interface DslParser {
    Element parse(DslRawEntry entry);
}


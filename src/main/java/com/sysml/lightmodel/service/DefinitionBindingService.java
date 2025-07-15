package com.sysml.lightmodel.service;

import com.sysml.lightmodel.semantic.Element;

public interface DefinitionBindingService {
    Element bind(String typeName, String expectedType);
}

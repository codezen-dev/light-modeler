package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefinitionResolver {

    private final Map<String, Element> nameToElement = new HashMap<>();
    private final Map<String, Element> idToElement = new HashMap<>();

    public DefinitionResolver(List<Element> allElements) {
        for (Element e : allElements) {
            if (e.getName() != null) {
                nameToElement.put(e.getName(), e);
            }
            if (e.getId() != null) {
                idToElement.put(e.getId().toString(), e);
            }
        }
    }

    public Element resolveByName(String name) {
        return nameToElement.get(name);
    }

    public Element resolveById(String id) {
        return idToElement.get(id);
    }
}


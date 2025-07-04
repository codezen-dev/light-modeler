package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefinitionResolver {

    private final Map<String, Element> idMap = new HashMap<>();
    private final Map<Long, Element> longIdMap = new HashMap<>();

    public DefinitionResolver(List<Element> allElements) {
        for (Element e : allElements) {
            if (e.getId() != null) {
                idMap.put(e.getId().toString(), e);
                longIdMap.put(e.getId(), e);
            }
        }
    }

    public Element resolveById(Object id) {
        if (id == null) return null;
        if (id instanceof Long) return longIdMap.get(id);
        return idMap.get(id.toString());
    }
}

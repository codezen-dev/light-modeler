package com.sysml.lightmodel.semantic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 将 definitionName → resolvedDefinition 映射补全
 */
public class DefinitionBinder {

    public static void bindAll(List<Element> elements) {
        Map<String, Element> nameMap = elements.stream()
                .filter(e -> e.getName() != null)
                .collect(Collectors.toMap(Element::getName, e -> e, (a, b) -> a));

        for (Element e : elements) {
            if (e instanceof Definition def && def.getOwnedUsages() != null) {
                for (Usage usage : def.getOwnedUsages()) {
                    String defName = usage.getDefinitionName();
                    if (defName != null && nameMap.containsKey(defName)) {
                        Element resolved = nameMap.get(defName);
                        if (resolved instanceof Definition d) {
                            usage.setResolvedDefinition(d);
                        }
                    }
                }
            }
        }
    }
}




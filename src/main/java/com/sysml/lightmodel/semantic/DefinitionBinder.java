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
            bindRecursive(e, nameMap);
        }
    }

    private static void bindRecursive(Element e, Map<String, Element> nameMap) {
        if (e.getChildren() != null) {
            for (Element child : e.getChildren()) {
                bindRecursive(child, nameMap);
            }
        }

        if (e.getMetadata() != null) {
            String defName = (String) e.getMetadata().get("definitionName");
            if (defName != null && nameMap.containsKey(defName)) {
                Element def = nameMap.get(defName);
                e.getMetadata().put("definition", def.getId()); // ✅ 持久化引用
            }
        }
    }
}



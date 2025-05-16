package com.sysml.lightmodel.semantic;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 根据 name 查询对应 Element 的 type，用于 DSL 中的引用解析
 */
@Component
public class DefinitionResolver {

    private static final Map<String, Element> nameToElement = new ConcurrentHashMap<>();

    public static void register(Element element) {
        if (element.getName() != null) {
            nameToElement.put(element.getName(), element);
        }
    }

    public static void clear() {
        nameToElement.clear();
    }

    public static String resolve(String definitionName) {
        Element target = nameToElement.get(definitionName);
        if (target == null) {
            return definitionName + " /* unresolved */";
        } else {
            return target.getName() + " (" + target.getType() + ")";
        }
    }
}

package com.sysml.lightmodel.semantic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ElementCache {

    // name -> Element 的缓存，用于解析 definition 等引用
    private static final Map<String, Element> nameToElementMap = new ConcurrentHashMap<>();

    public static void clear() {
        nameToElementMap.clear();
    }

    public static void put(Element element) {
        if (element.getName() != null) {
            nameToElementMap.put(element.getName(), element);
        }
    }

    public static Element get(String name) {
        return nameToElementMap.get(name);
    }

    public static boolean contains(String name) {
        return nameToElementMap.containsKey(name);
    }

    public static void init(java.util.List<Element> allElements) {
        clear();
        for (Element element : allElements) {
            put(element);
        }
    }
}

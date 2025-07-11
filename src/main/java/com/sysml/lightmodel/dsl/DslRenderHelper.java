package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.Element;

import java.util.*;

public class DslRenderHelper {

    public static String renderMultiplicity(Element element) {
        Object val = getMetadataValue(element, "multiplicity");
        return val != null ? "[" + val + "]" : "";
    }

    public static String renderMetadata(Element element) {
        Map<String, Object> metadata = element.getMetadata();
        if (metadata == null || metadata.isEmpty()) return "";
        List<String> entries = new ArrayList<>();
        for (var e : metadata.entrySet()) {
            if ("multiplicity".equals(e.getKey())) continue;
            entries.add(e.getKey() + " = \"" + e.getValue() + "\"");
        }
        return entries.isEmpty() ? "" : "{ " + String.join(", ", entries) + " }";
    }

    public static String renderDocumentation(Element element) {
        return element.getDocumentation() != null && !element.getDocumentation().isEmpty()
                ? "doc \"" + element.getDocumentation() + "\""
                : "";
    }

    private static Object getMetadataValue(Element element, String key) {
        return element.getMetadata() != null ? element.getMetadata().get(key) : null;
    }
}

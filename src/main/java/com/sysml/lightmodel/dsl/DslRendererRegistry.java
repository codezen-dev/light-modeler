package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.Element;

import java.util.HashMap;
import java.util.Map;

public class DslRendererRegistry {
    private static final Map<String, DslRenderer> registry = new HashMap<>();

    static {
        register("PartUsage", new PartUsageDslRenderer());
        register("AttributeUsage", new AttributeUsageDslRenderer());
        register("ConstraintUsage", new ConstraintUsageDslRenderer());
        register("StructureDefinition", new StructureDefinitionDslRenderer());
        register("ActionUsage", new ActionUsageDslRenderer());
        register("ValueDefinition", new ValueDefinitionDslRenderer());
        register("Default", new DefaultDslRenderer());
    }

    public static void register(String type, DslRenderer renderer) {
        registry.put(type, renderer);
    }

    @SuppressWarnings("unchecked")
    public static DslRenderer getRenderer(String type) {
        return  registry.getOrDefault(type, registry.get("Default"));
    }

    public static String render(Element element, int indent) {
        if (element == null || element.getType() == null) return "";
        return getRenderer(element.getType()).render(element, indent);
    }
}



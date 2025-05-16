package com.sysml.lightmodel.dsl;

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

    public static DslRenderer getRenderer(String type) {
        return registry.getOrDefault(type, registry.get("Default"));
    }
}

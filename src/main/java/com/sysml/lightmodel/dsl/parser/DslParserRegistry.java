package com.sysml.lightmodel.dsl.parser;

import java.util.HashMap;
import java.util.Map;

public class DslParserRegistry {

    private static final Map<String, DslParser> registry = new HashMap<>();

    static {
        register("StructureDefinition", new StructureDefinitionDslParser());
        register("AttributeUsage", new AttributeUsageDslParser());
        register("ConstraintUsage",new ConstraintUsageDslParser());
        register("PartUsage",new PartUsageDslParser());
    }

    public static void register(String type, DslParser parser) {
        registry.put(type, parser);
    }

    public static DslParser getParser(String type) {
        return registry.getOrDefault(type, raw -> null); // fallback 空
    }
}

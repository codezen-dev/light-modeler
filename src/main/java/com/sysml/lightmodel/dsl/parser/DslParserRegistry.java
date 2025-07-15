package com.sysml.lightmodel.dsl.parser;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DslParserRegistry {

    private final Map<String, DslParser> registry = new HashMap<>();

    public void register(String type, DslParser parser) {
        registry.put(type, parser);
    }

    public DslParser getParser(String type) {
        return registry.getOrDefault(type, raw -> null); // fallback 空
    }
}



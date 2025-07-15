package com.sysml.lightmodel.dsl.parser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class DslParserRegistrar {

    private final DslParserRegistry registry;

    // 所有解析器注入
    private final StructureDefinitionDslParser structureParser;
    private final AttributeUsageDslParser attributeParser;
    private final PartUsageDslParser partParser;
    private final ConstraintUsageDslParser constraintParser;

    @PostConstruct
    public void registerAll() {
        registry.register("StructureDefinition", structureParser);
        registry.register("AttributeUsage", attributeParser);
        registry.register("PartUsage", partParser);
        registry.register("ConstraintUsage", constraintParser);
    }
}


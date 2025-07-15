package com.sysml.lightmodel.dsl.parser;

import com.sysml.lightmodel.semantic.Definition;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.semantic.Usage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class StructureDefinitionDslParser implements DslParser {

    private final DslParserRegistry dslParserRegistry;

    @Override
    public Element parse(DslRawEntry entry) {
        Definition def = new Definition();
        def.setType("StructureDefinition");
        def.setName(entry.name);
        def.setOwnedUsages(new ArrayList<>());

        String[] lines = entry.body.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String prefix = extractPrefix(line);
            String semanticType = mapPrefixToType(prefix);

            DslParser parser = dslParserRegistry.getParser(semanticType);
            if (parser != null) {
                DslRawEntry subEntry = new DslRawEntry();
                subEntry.body = line;
                Element parsed = parser.parse(subEntry);
                if (parsed instanceof Usage usage) {
                    def.getOwnedUsages().add(usage);
                }
            }
        }

        return def;
    }

    private String extractPrefix(String line) {
        int spaceIndex = line.indexOf(" ");
        return (spaceIndex > 0) ? line.substring(0, spaceIndex) : "";
    }

    private String mapPrefixToType(String prefix) {
        return switch (prefix) {
            case "attr" -> "AttributeUsage";
            case "part" -> "PartUsage";
            case "constraint" -> "ConstraintUsage";
            default -> "";
        };
    }
}


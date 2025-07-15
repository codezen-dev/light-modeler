package com.sysml.lightmodel.dsl.parser;

import com.sysml.lightmodel.semantic.Definition;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.semantic.Usage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DslParseCoordinator {

    private final DslParserRegistry dslParserRegistry;

    public Element parseElement(DslRawEntry entry) {
        DslParser parser = dslParserRegistry.getParser(entry.type);
        Element element = parser.parse(entry);

        if (element instanceof Definition def && def.getMetadata() != null) {
            List<String> childLines = (List<String>) def.getMetadata().get("__childLines");
            if (childLines != null) {
                List<Usage> usages = new ArrayList<>();
                for (String line : childLines) {
                    String prefix = extractPrefix(line);
                    String type = mapPrefixToType(prefix);
                    DslParser subParser = dslParserRegistry.getParser(type);
                    if (subParser != null) {
                        DslRawEntry subEntry = new DslRawEntry();
                        subEntry.body = line;
                        Element parsed = subParser.parse(subEntry);
                        if (parsed instanceof Usage usage) {
                            usages.add(usage);
                        }
                    }
                }
                def.setOwnedUsages(usages);
                def.getMetadata().remove("__childLines");
            }
        }

        return element;
    }

    private String extractPrefix(String line) {
        int idx = line.indexOf(" ");
        return (idx > 0) ? line.substring(0, idx) : "";
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

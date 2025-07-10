package com.sysml.lightmodel.dsl.parser;

import com.sysml.lightmodel.semantic.Definition;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.semantic.Usage;

import java.util.ArrayList;

public class StructureDefinitionDslParser implements DslParser {

    @Override
    public Element parse(DslRawEntry entry) {
        Definition def = new Definition();
        def.setType("StructureDefinition");
        def.setName(entry.name);
        def.setOwnedUsages(new ArrayList<>());

        // 兼容 \n 和 \r\n
        String[] lines = entry.body.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();

            DslRawEntry subEntry = new DslRawEntry();
            subEntry.body = line;

            Element parsed = null;

            if (line.startsWith("attr ")) {
                parsed = new AttributeUsageDslParser().parse(subEntry);
            } else if (line.startsWith("part ")) {
                parsed = new PartUsageDslParser().parse(subEntry);
            } else if (line.startsWith("constraint ")) {
                parsed = new ConstraintUsageDslParser().parse(subEntry);
            }

            if (parsed instanceof Usage usage) {
                def.getOwnedUsages().add(usage);
            }
        }

        return def;
    }
}



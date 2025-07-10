package com.sysml.lightmodel.dsl.parser;

import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.semantic.PartUsage;
import com.sysml.lightmodel.utils.DslParseHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartUsageDslParser implements DslParser {

    private static final Pattern MAIN_PATTERN = Pattern.compile("part\\s+(\\w+)\\s*:\\s*(\\w+)");

    @Override
    public Element parse(DslRawEntry entry) {
        String line = entry.body.trim();
        Matcher matcher = MAIN_PATTERN.matcher(line);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid part line: " + line);
        }

        String name = matcher.group(1);
        String type = matcher.group(2);

        PartUsage usage = new PartUsage();
        usage.setType("PartUsage");
        usage.setName(name);
        usage.setDefinitionName(type);
        usage.setMetadata(DslParseHelper.enrichMetadata(line));
        usage.setDocumentation(DslParseHelper.parseDocumentation(line));

        return usage;
    }
}






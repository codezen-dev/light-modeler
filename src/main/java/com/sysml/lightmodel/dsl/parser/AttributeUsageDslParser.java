package com.sysml.lightmodel.dsl.parser;

import com.sysml.lightmodel.semantic.AttributeUsage;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.utils.DslParseHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AttributeUsageDslParser implements DslParser {

    private static final Pattern ATTR_PATTERN = Pattern.compile("attr\\s+(\\w+)\\s*:\\s*(\\w+)");

    @Override
    public Element parse(DslRawEntry entry) {
        String line = entry.body.trim();
        Matcher matcher = ATTR_PATTERN.matcher(line);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid attr line: " + line);
        }

        String name = matcher.group(1);
        String type = matcher.group(2);

        AttributeUsage usage = new AttributeUsage();
        usage.setType("AttributeUsage");
        usage.setName(name);
        usage.setDefinitionName(type);

        usage.setMetadata(DslParseHelper.enrichMetadata(line));
        usage.setDocumentation(DslParseHelper.parseDocumentation(line));

        return usage;
    }
}





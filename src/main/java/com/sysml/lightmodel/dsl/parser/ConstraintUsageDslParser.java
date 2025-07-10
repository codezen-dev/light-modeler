package com.sysml.lightmodel.dsl.parser;

import com.sysml.lightmodel.semantic.ConstraintUsage;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.utils.DslParseHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstraintUsageDslParser implements DslParser {

    private static final Pattern MAIN_PATTERN = Pattern.compile("constraint\\s+(\\w+)\\s*:\\s*(\\w+)");

    @Override
    public Element parse(DslRawEntry entry) {
        String line = entry.body.trim();
        Matcher matcher = MAIN_PATTERN.matcher(line);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid constraint line: " + line);
        }

        String name = matcher.group(1);
        String type = matcher.group(2);

        ConstraintUsage usage = new ConstraintUsage();
        usage.setType("ConstraintUsage");
        usage.setName(name);
        usage.setDefinitionName(type);


        // 加入 multiplicity
        usage.setMetadata(DslParseHelper.enrichMetadata(line));
        usage.setDocumentation(DslParseHelper.parseDocumentation(line));

        return usage;
    }
}






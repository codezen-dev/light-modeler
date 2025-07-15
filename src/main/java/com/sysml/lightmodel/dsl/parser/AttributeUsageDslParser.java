package com.sysml.lightmodel.dsl.parser;

import com.sysml.lightmodel.semantic.AttributeUsage;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.service.DefinitionBindingService;
import com.sysml.lightmodel.utils.DslParseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AttributeUsageDslParser implements DslParser {

    @Autowired
    private DefinitionBindingService definitionBindingService;

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

        Element def = definitionBindingService.bind(type, "AttributeDefinition");
        usage.setDefinitionId(def.getId());
        usage.setDefinitionName(def.getName());
        usage.setResolvedDefinition(def);
        usage.setMetadata(DslParseHelper.enrichMetadata(line));
        usage.setDocumentation(DslParseHelper.parseDocumentation(line));

        return usage;
    }
}





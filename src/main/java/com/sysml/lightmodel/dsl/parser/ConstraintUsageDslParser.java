package com.sysml.lightmodel.dsl.parser;

import com.sysml.lightmodel.semantic.ConstraintUsage;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.service.DefinitionBindingService;
import com.sysml.lightmodel.utils.DslParseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ConstraintUsageDslParser implements DslParser {

    @Autowired
    private DefinitionBindingService definitionBindingService;
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
        Element def = definitionBindingService.bind(type, "AttributeDefinition");
        usage.setDefinitionId(def.getId());
        usage.setDefinitionName(def.getName());
        usage.setResolvedDefinition(def);

        // 加入 multiplicity
        usage.setMetadata(DslParseHelper.enrichMetadata(line));
        usage.setDocumentation(DslParseHelper.parseDocumentation(line));

        return usage;
    }
}






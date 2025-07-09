package com.sysml.lightmodel.service.impl;

import com.sysml.lightmodel.semantic.Definition;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.semantic.TypeLibraryElement;
import com.sysml.lightmodel.semantic.Usage;
import com.sysml.lightmodel.service.DslImportService;
import com.sysml.lightmodel.service.TypeLibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DefaultDslImportService implements DslImportService {

    private final TypeLibraryService typeLibraryService;

    private static final Pattern IMPORT_PATTERN = Pattern.compile("import\\s+\"([^\"]+)\"");
    private static final Pattern DEF_PATTERN = Pattern.compile("def\\s+(\\w+)\\s+(\\w+)\\s*\\{([\\s\\S]*?)\\}", Pattern.MULTILINE);
    private static final Pattern USAGE_PATTERN = Pattern.compile("(part|attr)\\s+(\\w+):\\s*(\\w+)(\\[[^\\]]*])?\\s*(=\\s*[^\\n]+)?");

    @Override
    public List<Element> parseDsl(String dslText) {
        List<Element> allElements = getElements(dslText);

        Matcher defMatcher = DEF_PATTERN.matcher(dslText);
        while (defMatcher.find()) {
            String kind = defMatcher.group(1);
            String name = defMatcher.group(2);
            String body = defMatcher.group(3).trim();

            Definition def = new Definition();
            def.setType(kind);
            def.setName(name);

            List<Usage> usages = parseUsages(body, def);
            def.setOwnedUsages(usages);

            allElements.add(def);
        }

        return allElements;
    }

    private List<Element> getElements(String dslText) {
        List<Element> allElements = new ArrayList<>();

        // ✅ 1. 处理 import 语句
        Matcher importMatcher = IMPORT_PATTERN.matcher(dslText);
        while (importMatcher.find()) {
            String libName = importMatcher.group(1);

            // ✅ 映射 sys-types → default 类型库
            if ("sys-types".equals(libName)) {
                libName = "default";
            }

            // 加载当前库中的所有类型定义
            List<TypeLibraryElement> typeDefs = typeLibraryService.getAllTypeDefinitions();
            for (TypeLibraryElement libType : typeDefs) {
                allElements.add(toElement(libType));
            }

        }
        return allElements;
    }

    private Element toElement(TypeLibraryElement src) {
        Element e = new Element();
        e.setName(src.getName());
        e.setType(src.getType());
        e.setDocumentation(src.getDocumentation());
        e.setChildren(src.getChildren());
        return e;
    }


    private List<Usage> parseUsages(String body, Element parent) {
        List<Usage> usages = new ArrayList<>();
        Matcher usageMatcher = USAGE_PATTERN.matcher(body);

        while (usageMatcher.find()) {
            String kind = usageMatcher.group(1); // part or attr
            String name = usageMatcher.group(2);
            String type = usageMatcher.group(3);
            String multiplicity = usageMatcher.group(4); // [0..*]
            String defaultValue = usageMatcher.group(5); // = xxx

            Usage usage = new Usage();
            usage.setName(name);
            usage.setType(kind.equals("part") ? "PartUsage" : "AttributeUsage");
            usage.setDefinitionName(type);

            if (parent instanceof Definition def) {
                usage.setParentDefinition(def);
            }

            if (multiplicity != null) {
                usage.setMultiplicity(multiplicity.replaceAll("[\\[\\]]", "").trim());
            }

            if (defaultValue != null) {
                usage.setDefaultValue(defaultValue.replace("=", "").trim());
            }

            usages.add(usage);
        }

        return usages;
    }
}


package com.sysml.lightmodel.service.impl;

import com.sysml.lightmodel.semantic.*;
import com.sysml.lightmodel.service.DslDocumentService;
import com.sysml.lightmodel.service.DslImportService;
import com.sysml.lightmodel.service.SemanticElementService;
import com.sysml.lightmodel.service.TypeLibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultDslImportService implements DslImportService {

    private final TypeLibraryService typeLibraryService;
    private final SemanticElementService elementService;
    private final DslDocumentService dslDocumentService;

    private static final Pattern IMPORT_PATTERN = Pattern.compile("import\\s+\"([^\"]+)\"");
    private static final Pattern DEF_PATTERN = Pattern.compile("def\\s+(\\w+)\\s+(\\w+)\\s*\\{([\\s\\S]*?)\\}", Pattern.MULTILINE);
    private static final Pattern USAGE_PATTERN = Pattern.compile("(part|attr)\\s+(\\w+):\\s*(\\w+)(\\[[^\\]]*])?\\s*(=\\s*[^\\n]+)?");

    @Override
    public List<Element> parseDsl(String dslText) {
        List<Element> allElements = new ArrayList<>();

        // ✅ 加载默认库类型（如 PrimitiveType）
        Matcher importMatcher = IMPORT_PATTERN.matcher(dslText);
        while (importMatcher.find()) {
            String libName = importMatcher.group(1);
            if ("sys-types".equals(libName)) libName = "default";

            List<TypeLibraryElement> typeDefs = typeLibraryService.getAllTypeDefinitions();
            for (TypeLibraryElement libType : typeDefs) {
                allElements.add(toElement(libType));
            }
        }

        // ✅ 解析 DSL 中定义的元素
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
            def.setChildren(new ArrayList<>(usages)); //
            allElements.add(def);
        }

        return allElements;
    }

    @Override
    @Transactional
    public List<Element> importDslWithPersistence(String dslText) {
        List<Element> elements = parseDsl(dslText);
        DefinitionBinder.bindAll(elements);

        // ✅ 数据库中已有的元素
        List<Element> existingDbElements = elementService.getAllElements();
        Map<String, Element> existingMap = existingDbElements.stream()
                .filter(e -> e.getName() != null && e.getType() != null)
                .collect(Collectors.toMap(e -> e.getName() + ":" + e.getType(), e -> e, (a, b) -> a));

        Set<String> existingKeys = new HashSet<>(existingMap.keySet());
        List<Element> toSave = new ArrayList<>();

        for (Element e : elements) {
            if (e instanceof Definition def) {
                String defKey = def.getName() + ":" + def.getType();
                if (!existingKeys.contains(defKey)) {
                    def.setId(null); // 🔒 清空 ID，避免主键冲突
                    toSave.add(def);
                    existingKeys.add(defKey);
                }

                if (def.getOwnedUsages() != null) {
                    for (Usage usage : def.getOwnedUsages()) {
                        usage.setId(null);
                        usage.setOwner(def.getId() == null ? null : def.getId().toString());
                        String usageKey = usage.getName() + ":" + usage.getType() + ":" + def.getName(); // 避免重名重复
                        if (!existingKeys.contains(usageKey)) {
                            toSave.add(usage);
                            existingKeys.add(usageKey);
                        }
                    }
                }
            } else {
                String key = e.getName() + ":" + e.getType();
                if (!existingKeys.contains(key)) {
                    e.setId(null);
                    toSave.add(e);
                    existingKeys.add(key);
                }
            }
        }

        // ✅ 自动绑定 orphan Usage 的 owner
        for (Element e : toSave) {
            if (e instanceof Usage u && u.getOwner() == null) {
                Definition parent = findParentDefinition(elements, u);
                if (parent != null) {
                    u.setOwner(parent.getId() == null ? null : parent.getId().toString());
                }
            }
        }

        // ✅ 执行插入
        List<Long> savedIds = new ArrayList<>();
        for (Element e : toSave) {
            Element saved = elementService.createElement(e);
            savedIds.add(saved.getId());
        }

        dslDocumentService.saveDsl("Unnamed", dslText, savedIds);
        return elements;
    }


    private Element toElement(TypeLibraryElement src) {
        Element e = new Element();
        e.setName(src.getName());
        e.setType(src.getType());
        e.setDocumentation(src.getDocumentation());
        return e;
    }

    private List<Usage> parseUsages(String body, Definition parent) {
        List<Usage> usages = new ArrayList<>();
        Matcher matcher = USAGE_PATTERN.matcher(body);
        while (matcher.find()) {
            String kind = matcher.group(1);
            String name = matcher.group(2);
            String defName = matcher.group(3);
            String multiplicity = matcher.group(4);
            String defaultValue = matcher.group(5);

            Usage usage = new Usage();
            usage.setName(name);
            usage.setType(kind.equals("attr") ? "AttributeUsage" : "PartUsage");
            usage.setDefinitionName(defName);
            usage.setMultiplicity(multiplicity != null ? multiplicity.replaceAll("[\\[\\]]", "") : null);
            usage.setDefaultValue(defaultValue != null ? defaultValue.replace("=", "").trim() : null);
            usage.setParentDefinition(parent);

            usages.add(usage);
        }
        return usages;
    }

    private Definition findParentDefinition(List<Element> elements, Usage usage) {
        for (Element e : elements) {
            if (e instanceof Definition def && def.getOwnedUsages() != null) {
                if (def.getOwnedUsages().contains(usage)) {
                    return def;
                }
            }
        }
        return null;
    }
}




package com.sysml.lightmodel.service.impl;

import com.sysml.lightmodel.dsl.DefinitionResolver;
import com.sysml.lightmodel.dsl.DslRendererRegistry;
import com.sysml.lightmodel.dsl.RendererContext;
import com.sysml.lightmodel.semantic.Definition;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.semantic.TypeLibraryElement;
import com.sysml.lightmodel.semantic.Usage;
import com.sysml.lightmodel.service.DSLService;
import com.sysml.lightmodel.service.SemanticElementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DSLServiceImpl implements DSLService {

    private final SemanticElementService elementService;

    private final TypeLibraryServiceImpl typeLibraryService;
    @Override
    public String exportDsl() {
        List<Element> modelElements = elementService.getAllElements();
        List<Element> roots = elementService.getElementTree();

        // 加载类型库定义
        List<TypeLibraryElement> libDefs = typeLibraryService.getAllTypeDefinitions();
        List<Element> libElements = libDefs.stream()
                .flatMap(e -> Optional.ofNullable(e.getChildren())
                        .orElse(Collections.emptyList()).stream())
                .collect(Collectors.toList());


        // 合并后解析上下文
        List<Element> all = new ArrayList<>(modelElements);
        all.addAll(libElements);
        populateDefinitionNames(all);
        RendererContext.setResolver(new DefinitionResolver(all));

        try {
            StringBuilder builder = new StringBuilder();

            Set<String> imports = detectUsedLibraries(modelElements, typeLibraryService);
            for (String lib : imports) {
                builder.append("import \"").append(lib).append("\"\n\n");
            }

            for (Element root : roots) {
                String dsl = DslRendererRegistry.getRenderer(root.getType()).render(root, 0);
                builder.append(dsl);
            }

            return builder.toString();
        } finally {
            RendererContext.clear();
        }
    }

    @Override
    public String exportDsl(Long id) {
        List<Element> all = elementService.getAllElements();
        Element root = elementService.getElementTree(id);
        if (root == null) return "// 节点不存在";
        populateDefinitionNames(all);
        RendererContext.setResolver(new DefinitionResolver(all));
        try {
            return DslRendererRegistry.getRenderer(root.getType()).render(root, 0);
        } finally {
            RendererContext.clear();
        }
    }

    private Set<String> detectUsedLibraries(List<Element> allElements, TypeLibraryServiceImpl typeLibraryService) {
        Set<String> usedTypes = new HashSet<>();

        for (Element element : allElements) {
            if (element instanceof Definition def && def.getOwnedUsages() != null) {
                for (Usage usage : def.getOwnedUsages()) {
                    if (usage.getResolvedDefinition() != null) {
                        String typeName = usage.getResolvedDefinition().getName();
                        usedTypes.add(typeName);
                    }
                }
            }
        }

        // 匹配这些类型来自哪个库（目前只判断 default 库）
        Set<String> imports = new HashSet<>();
        List<TypeLibraryElement> defaultTypes = typeLibraryService.getAllTypeDefinitions();
        Set<String> defaultTypeNames = defaultTypes.stream()
                .map(TypeLibraryElement::getName)
                .collect(Collectors.toSet());

        for (String usedType : usedTypes) {
            if (defaultTypeNames.contains(usedType)) {
                imports.add("sys-types");
                break;
            }
        }

        return imports;
    }
    private void populateDefinitionNames(List<Element> allElements) {
        Map<String, Element> idToElement = allElements.stream()
                .filter(e -> e.getId() != null)
                .collect(Collectors.toMap(e -> e.getId().toString(), e -> e));

        for (Element element : allElements) {
            if (element.getMetadata() == null) continue;
            Object defId = element.getMetadata().get("definitionId");
            if (defId instanceof String id && idToElement.containsKey(id)) {
                element.setDefinitionName(idToElement.get(id).getName());
            }
        }
    }


}

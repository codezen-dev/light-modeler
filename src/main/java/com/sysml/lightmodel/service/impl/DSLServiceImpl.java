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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DSLServiceImpl implements DSLService {

    private final SemanticElementService elementService;

    private final TypeLibraryServiceImpl typeLibraryService;
    @Override
    public String exportDsl() {
        List<Element> all = elementService.getAllElements();
        List<Element> roots = elementService.getElementTree();

        // 注入上下文
        RendererContext.setResolver(new DefinitionResolver(all));
        try {
            StringBuilder builder = new StringBuilder();
            Set<String> imports = detectUsedLibraries(all, typeLibraryService);
            for (String lib : imports) {
                builder.append("import \"").append(lib).append("\"\n\n");
            }
            for (Element root : roots) {
                String dsl = DslRendererRegistry.getRenderer(root.getType()).render(root, 0);
                builder.append(dsl);
            }
            return builder.toString();
        } finally {
            RendererContext.clear(); // 清理上下文，防止线程复用污染
        }
    }
    @Override
    public String exportDsl(Long id) {
        List<Element> all = elementService.getAllElements();
        Element root = elementService.getElementTree(id);
        if (root == null) return "// 节点不存在";

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


}

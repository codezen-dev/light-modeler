package com.sysml.lightmodel.service.impl;

import com.sysml.lightmodel.dsl.DefinitionResolver;
import com.sysml.lightmodel.dsl.DslRendererRegistry;
import com.sysml.lightmodel.dsl.RendererContext;
import com.sysml.lightmodel.pojo.DslDocument;
import com.sysml.lightmodel.semantic.Definition;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.semantic.TypeLibraryElement;
import com.sysml.lightmodel.semantic.Usage;
import com.sysml.lightmodel.service.DSLService;
import com.sysml.lightmodel.service.DslDocumentService;
import com.sysml.lightmodel.service.SemanticElementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DSLServiceImpl implements DSLService {

    private final SemanticElementService elementService;

    private final TypeLibraryServiceImpl typeLibraryService;

    private final DslDocumentService dslDocumentService;
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
            StringBuilder builder = new StringBuilder();

            // ✅ 优先从 dslDocumentService 中获取原始 import 信息
            DslDocument doc = dslDocumentService.findByRootId(id);
            if (doc != null && doc.getContent() != null) {
                // 从原始 DSL 中提取 import "xxx"
                Pattern importPattern = Pattern.compile("import\\s+\"([^\"]+)\"");
                Matcher matcher = importPattern.matcher(doc.getContent());
                while (matcher.find()) {
                    builder.append("import \"").append(matcher.group(1)).append("\"\n");
                }
                builder.append("\n");
            } else {
                // fallback: 自动推导（兼容旧数据）
                Set<String> imports = detectUsedLibraries(List.of(root), typeLibraryService);
                for (String lib : imports) {
                    builder.append("import \"").append(lib).append("\"\n");
                }
                builder.append("\n");
            }

            // ✅ 渲染结构体
            String dsl = DslRendererRegistry.getRenderer(root.getType()).render(root, 0);
            builder.append(dsl);

            return builder.toString();
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

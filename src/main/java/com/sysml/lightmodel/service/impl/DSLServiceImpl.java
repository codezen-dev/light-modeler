package com.sysml.lightmodel.service.impl;

import com.sysml.lightmodel.dsl.DefinitionResolver;
import com.sysml.lightmodel.dsl.DslRendererRegistry;
import com.sysml.lightmodel.dsl.RendererContext;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.service.DSLService;
import com.sysml.lightmodel.service.SemanticElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DSLServiceImpl implements DSLService {

    @Autowired
    private SemanticElementService elementService;

    @Override
    public String exportDsl() {
        List<Element> all = elementService.getAllElements();
        List<Element> roots = elementService.getElementTree();

        // 注入上下文
        RendererContext.setResolver(new DefinitionResolver(all));
        try {
            StringBuilder builder = new StringBuilder();
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

}

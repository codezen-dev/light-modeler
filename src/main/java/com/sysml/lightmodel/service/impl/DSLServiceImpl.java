package com.sysml.lightmodel.service.impl;

import com.sysml.lightmodel.dsl.DslRendererRegistry;
import com.sysml.lightmodel.semantic.DefinitionResolver;
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
        DefinitionResolver.clear();
        for (Element element : elementService.getAllElements()) {
            DefinitionResolver.register(element);
        }

        List<Element> roots = elementService.getElementTree();
        StringBuilder builder = new StringBuilder();
        for (Element root : roots) {
            String dsl = DslRendererRegistry.getRenderer(root.getType()).render(root, 0);
            builder.append(dsl);
        }
        return builder.toString();
    }
}

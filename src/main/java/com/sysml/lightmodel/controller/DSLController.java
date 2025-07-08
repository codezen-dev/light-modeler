package com.sysml.lightmodel.controller;

import com.sysml.lightmodel.semantic.DefinitionBinder;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.service.DSLService;
import com.sysml.lightmodel.service.DslDocumentService;
import com.sysml.lightmodel.service.DslImportService;
import com.sysml.lightmodel.service.SemanticElementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dsl")
public class DSLController {

    private final DSLService dslService;

    private final DslImportService dslImportService;

    private final SemanticElementService elementService;

    private final DslDocumentService dslDocumentService;

    @GetMapping("/export")
    public String exportDsl() {
        return dslService.exportDsl();
    }

    @GetMapping("/export/{id}")
    public String exportDslById(@PathVariable Long id) {
        return dslService.exportDsl(id);
    }

    @PostMapping("/import")
    public List<Element> importDsl(@RequestBody String dslText) {
        List<Element> elements = dslImportService.parseDsl(dslText);
        DefinitionBinder.bindAll(elements);
        // 只保留尚未入库的类型（按 name + type 去重）
        Set<String> existingKeys = elementService.getAllElements().stream()
                .map(e -> e.getName() + ":" + e.getType())
                .collect(Collectors.toSet());

        List<Element> toSave = elements.stream()
                .filter(e -> !existingKeys.contains(e.getName() + ":" + e.getType()))
                .collect(Collectors.toList());
        // Step 2: 保存每个 Element，并记录它的 ID
        List<Long> elementIds = new ArrayList<>();
        for (Element e : toSave) {
            Element saved = elementService.createElement(e);
            elementIds.add(saved.getId());
        }
        dslDocumentService.saveDsl("Unnamed", dslText, elementIds);
        return elements;
    }

}


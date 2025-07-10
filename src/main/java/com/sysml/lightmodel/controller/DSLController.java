package com.sysml.lightmodel.controller;

import com.sysml.lightmodel.semantic.DefinitionBinder;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.service.DSLService;
import com.sysml.lightmodel.service.DslImportService;
import com.sysml.lightmodel.service.SemanticElementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dsl")
public class DSLController {

    private final DSLService dslService;

    private final DslImportService dslImportService;

    private final SemanticElementService elementService;

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
        for (Element element : elements) {
            elementService.createElement(element);
        }
        return elements;
    }

}


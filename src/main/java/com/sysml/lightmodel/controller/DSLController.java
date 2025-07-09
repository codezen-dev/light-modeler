package com.sysml.lightmodel.controller;

import com.sysml.lightmodel.semantic.Definition;
import com.sysml.lightmodel.semantic.DefinitionBinder;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.semantic.Usage;
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
        return dslImportService.importDslWithPersistence(dslText);
    }

}


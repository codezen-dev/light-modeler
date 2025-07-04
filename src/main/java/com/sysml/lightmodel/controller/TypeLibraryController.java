package com.sysml.lightmodel.controller;


import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.semantic.TypeLibraryElement;
import com.sysml.lightmodel.service.TypeLibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/type-library")
@RequiredArgsConstructor
public class TypeLibraryController {

    private final TypeLibraryService typeLibraryService;

    @GetMapping("/structure-definitions")
    public List<TypeLibraryElement> getStructureTypes() {
        return typeLibraryService.getStructureDefinitions();
    }

    @GetMapping("/current")
    public String getCurrentLibrary() {
        return typeLibraryService.getCurrentLibraryId();
    }

    @PostMapping("/switch/{libId}")
    public String switchLibrary(@PathVariable String libId) {
        typeLibraryService.switchLibrary(libId);
        return "ok";
    }

    @GetMapping("/list")
    public List<TypeLibraryElement> listAll() {
        return typeLibraryService.getAllTypeDefinitions();
    }

    @GetMapping("/load/{id}")
    public TypeLibraryElement load(@PathVariable String id) {
        return typeLibraryService.getTypeDefinitionById(id);
    }

    @PostMapping("/import")
    public String importLibrary(@RequestBody List<TypeLibraryElement> elements) {
        typeLibraryService.importDefinitions(elements);
        return "ok";
    }

    @PostMapping("/use/{id}")
    public List<Element> useType(@PathVariable String id) {
        return typeLibraryService.cloneElementsFromType(id);
    }
}
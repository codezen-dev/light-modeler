package com.sysml.lightmodel.controller;

import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.service.SemanticElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/elements")
public class SemanticElementController {

    @Autowired
    private SemanticElementService elementService;

    @GetMapping("/types")
    public List<Map<String, Object>> getAllReferenceableTypes() {
        List<Element> elements = elementService.getReferenceableTypes();
        return elements.stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", e.getId());
                    map.put("name", e.getName());
                    map.put("type", e.getType());
                    return map;
                }) .collect(Collectors.toList());
    }




    @PostMapping
    public Element create(@RequestBody Element element) {
        return elementService.createElement(element);
    }

    @PutMapping("/{id}")
    public Element update(@PathVariable Long id, @RequestBody Element element) {
        element.setId(id);
        return elementService.updateElement(element);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return elementService.deleteElement(id);
    }

    @GetMapping("/{id}")
    public Element getById(@PathVariable Long id) {
        return elementService.getElementById(id);
    }

    @GetMapping
    public List<Element> getAll() {
        return elementService.getAllElements();
    }

    @GetMapping("/tree")
    public List<Element> getTree() {
        return elementService.getElementTree();
    }
}

package com.sysml.lightmodel.controller;

import com.sysml.lightmodel.service.DSLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dsl")
public class DSLController {

    @Autowired
    private DSLService dslService;

    @GetMapping("/export")
    public String exportDsl() {
        return dslService.exportDsl();
    }
}


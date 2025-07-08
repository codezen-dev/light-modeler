package com.sysml.lightmodel.service;

import com.sysml.lightmodel.pojo.DslDocument;

import java.util.List;

public interface DslDocumentService {
    DslDocument saveDsl(String name, String content, List<Long> elementIds);
    List<DslDocument> getAll();
    DslDocument getById(Long id);
}
